package com.example.community.service;

import com.example.community.common.exception.custom.BadRequestException;
import com.example.community.common.exception.custom.ConflictedException;
import com.example.community.common.exception.custom.ResourceNotFoundException;
import com.example.community.common.exception.custom.UnauthorizedException;
import com.example.community.dto.request.auth.LoginRequest;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.request.users.UserNicknameUpdateRequest;
import com.example.community.dto.request.users.UserPasswordUpdateRequest;
import com.example.community.dto.request.users.UserUpdateRequest;
import com.example.community.dto.response.auth.LoginResponse;
import com.example.community.dto.response.auth.RegisterResponse;
import com.example.community.dto.response.users.UserResponse;
import com.example.community.entity.Users;
import com.example.community.repository.UsersRepository;
import com.example.community.security.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsersService {

    private final PasswordEncoder encoder;
    private final UsersRepository usersRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    // 1. 회원가입
    // 1-1. 이메일 중복 체크
    public void isExistUserEmail(String email) {
        if (usersRepository.existsByEmail(email)) {
            throw new ConflictedException("이미 사용 중인 이메일입니다.");
        }
    }

    // 1-2. 닉네임 중복 체크
    public void isExistUserNickname(String nickname) {
        if (usersRepository.existsByNickname(nickname)) {
            throw new ConflictedException("이미 사용 중인 닉네임입니다.");
        }
    }

    // 1-3. 회원가입 구현
    public RegisterResponse register(RegisterRequest registerRequest) {
        // 1) 이메일 중복 확인
        isExistUserEmail(registerRequest.getEmail());

        // 2) 닉네임 중복 확인
        isExistUserNickname(registerRequest.getNickname());

        // 3) 패스워드 일치하는지 체크
        checkPassword(registerRequest.getPassword(), registerRequest.getPasswordCheck());

        // 4) 패스워드 암호화
        String encodePassword = encoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encodePassword);

        // 5) 저장
        Users saveUser = usersRepository.save(
                RegisterRequest.ofEntity(registerRequest));
        return RegisterResponse.fromEntity(saveUser);

    }

    // 비밀번호 일치 검사
    private void checkPassword(String password, String passwordCheck) {
        if (password == null || passwordCheck == null) {
            throw new BadRequestException("비밀번호를 입력 안함");
        }
        if (!password.equals(passwordCheck)) {
            throw new BadRequestException("패스워드 불일치");
        }
    }

    // 2. 로그인 구현
    public LoginResponse login(LoginRequest loginRequest) {
        // 1) 사용자 인증
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        // 2) 응답은 Users 엔티티 기준 (이메일로 조회)
        Users user = usersRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 이메일입니다."));

        // 3) 토큰 생성 (email, role 문자열)
        String token = jwtTokenUtil.generateToken(
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );

        // 4) 응답
        return LoginResponse.fromEntity(user, token);
    }

    // 사용자 인증
    private void authenticate(String email, String pwd) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pwd));
        } catch (DisabledException e) { // 계정 비활성화, 이메일 인증 미완료
            throw new UnauthorizedException("인증되지 않은 아이디입니다.");
        } catch (BadCredentialsException e) { // 로그인 실패 - 비밀번호 틀리거나 사용자 없음
            throw new UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 비밀번호 인코딩 체크
    // 사용자가 입력한 비밀번호와 디비에 저장된 비밀번호가 같은지 체크
    private void checkEncodePassword(String rawPassword, String encodedPassword) {
        if (!encoder.matches(rawPassword, encodedPassword)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 3. 마이페이지
    // 3-1. 사용자 정보 조회
    public UserResponse check(Users users, String password) {
        checkEncodePassword(password, users.getPassword()); // 비밀번호 검증 후 정보 리턴
        return UserResponse.fromEntity(users);
    }

    // 3-2. 사용자 정보 수정 - 비밀번호 수정
    public UserResponse updatePassword(Users user, UserPasswordUpdateRequest request) {

        // 비밀번호 바꿀 사용자 찾기
        Users updatePasswordUser = usersRepository.findByEmail(user.getEmail()). orElseThrow(
                () -> new ResourceNotFoundException("사용자를 찾을 수 없습니다.") // 존쟈하지 않는 자원(사용자)
        );

        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경
        if(request.getNewPassword() != null || request.getNewPasswordCheck() != null) {
            checkPassword(request.getNewPassword(), request.getNewPasswordCheck());
            String encodedPassword = encoder.encode(request.getNewPassword());
            updatePasswordUser.changePassword(encodedPassword);
        }

        // 리턴
        return UserResponse.fromEntity(updatePasswordUser);
    }

    // 3-2. 사용자 정보 수정 - 닉네임 수정
    public UserResponse updateNickname(Users user, UserNicknameUpdateRequest request) {

        // 닉네임 바꿀 사용자 찾기
        Users updateNicknameUser = usersRepository.findByEmail(user.getEmail()). orElseThrow(
                () -> new ResourceNotFoundException("사용자를 찾을 수 없습니다.") // 존쟈하지 않는 자원(사용자)
        );

        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        // 닉네임 변경
        if (!user.getNickname().equals(request.getNickname())) {
            updateNicknameUser.changeNickname(request.getNickname());
        }

        // 리턴
        return UserResponse.fromEntity(updateNicknameUser);
    }

    public Users getById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }


    // 4. 마이페이지 - 관리자, 회원 탈퇴
    // soft delete
    public void delete(Long id) { usersRepository.deleteById(id); }

    // 5. 관리자 - 회원 전체 조회
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<Users> users = usersRepository.findAll(pageable);
        List<UserResponse> list = users.getContent().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, users.getTotalElements());
    }

}