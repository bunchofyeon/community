package com.example.community.service;

import com.example.community.common.exception.custom.BadRequestException;
import com.example.community.common.exception.custom.ConflictedException;
import com.example.community.common.exception.custom.ResourceNotFoundException;
import com.example.community.common.exception.custom.UnauthorizedException;
import com.example.community.dto.request.auth.LoginRequest;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.request.users.UserNicknameUpdateRequest;
import com.example.community.dto.request.users.UserPasswordUpdateRequest;
import com.example.community.dto.response.auth.LoginResponse;
import com.example.community.dto.response.auth.RegisterResponse;
import com.example.community.dto.response.users.UserResponse;
import com.example.community.entity.Role;
import com.example.community.entity.Users;
import com.example.community.repository.UsersRepository;
import com.example.community.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UsersService usersService;

    @Test
    @DisplayName("이메일 중복 - ConflictedException")
    void duplicateEmail() {
        // given
        String email = "test@example.com";
        when(usersRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> usersService.isExistUserEmail(email))
                .isInstanceOf(ConflictedException.class);

        verify(usersRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 아님")
    void notDuplicateEmail() {
        // given
        String email = "test@example.com";
        when(usersRepository.existsByEmail(email)).thenReturn(false);

        // when & then (예외 안 나면 성공)
        assertThatCode(() -> usersService.isExistUserEmail(email))
                .doesNotThrowAnyException();
        verify(usersRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("닉네임 중복이면 ConflictedException 발생")
    void isExistUserNickname_whenExists_throwConflictedException() {
        // given
        String nickname = "piney";
        when(usersRepository.existsByNickname(nickname)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> usersService.isExistUserNickname(nickname))
                .isInstanceOf(ConflictedException.class);

        verify(usersRepository, times(1)).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임이 중복이 아니면 예외 없이 통과")
    void isExistUserNickname_whenNotExists_doNothing() {
        // given
        String nickname = "piney";
        when(usersRepository.existsByNickname(nickname)).thenReturn(false);

        // when & then
        assertThatCode(() -> usersService.isExistUserNickname(nickname))
                .doesNotThrowAnyException();
        verify(usersRepository, times(1)).existsByNickname(nickname);
    }

    // ===========================
    // 2. 회원가입
    // ===========================

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .nickname("piney")
                .password("1234")
                .passwordCheck("1234")
                .build();

        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usersRepository.existsByNickname(request.getNickname())).thenReturn(false);

        when(passwordEncoder.encode("1234")).thenReturn("encoded-1234");

        Users savedUser = Users.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password("encoded-1234")
                .build();

        // RegisterRequest.ofEntity(registerRequest)에서 만들어진 Users를 save하는데,
        // 여기서는 어떤 Users가 와도 savedUser 반환
        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

        // when
        RegisterResponse response = usersService.register(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("piney");
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복 시 ConflictedException")
    void register_fail_whenEmailDuplicated() {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .nickname("piney")
                .password("1234")
                .passwordCheck("1234")
                .build();

        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> usersService.register(request))
                .isInstanceOf(ConflictedException.class);
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치 시 BadRequestException")
    void register_fail_whenPasswordNotMatch() {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .nickname("piney")
                .password("1234")
                .passwordCheck("5678")
                .build();

        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usersRepository.existsByNickname(request.getNickname())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> usersService.register(request))
                .isInstanceOf(BadRequestException.class);
    }

    // ===========================
    // 3. 로그인
    // ===========================

    @Test
    @DisplayName("로그인 성공 - 토큰 발급")
    void login_success() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("1234")
                .build();

        Users user = Users.builder()
                .email("test@example.com")
                .password("encoded-1234")
                .role(Role.USER)
                .build();

        when(usersRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtTokenUtil.generateToken(eq("test@example.com"), anyString()))
                .thenReturn("fake-jwt-token");

        // when
        LoginResponse response = usersService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(authenticationManager).authenticate(any());
        verify(jwtTokenUtil).generateToken(eq("test@example.com"), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 인증 실패 시 UnauthorizedException")
    void login_fail_whenBadCredentials() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("wrong@example.com")
                .password("wrong")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        // when & then
        assertThatThrownBy(() -> usersService.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 미존재 시 ResourceNotFoundException")
    void login_fail_whenEmailNotFound() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("notfound@example.com")
                .password("1234")
                .build();

        // 인증은 통과한다고 치고
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(usersRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> usersService.login(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===========================
    // 4. 마이페이지 - 내 정보 확인(check)
    // ===========================

    @Test
    @DisplayName("마이페이지 - 비밀번호 일치 시 내 정보 조회 성공")
    void check_success() {
        // given
        Users user = Users.builder()
                .email("test@example.com")
                .password("encoded-1234")
                .nickname("piney")
                .build();

        String rawPassword = "1234";

        when(passwordEncoder.matches(rawPassword, user.getPassword()))
                .thenReturn(true);

        // when
        UserResponse response = usersService.check(user, rawPassword);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("piney");
    }

    @Test
    @DisplayName("마이페이지 - 비밀번호 불일치 시 UnauthorizedException")
    void check_fail_whenPasswordNotMatch() {
        // given
        Users user = Users.builder()
                .email("test@example.com")
                .password("encoded-1234")
                .nickname("piney")
                .build();

        String rawPassword = "wrong";

        when(passwordEncoder.matches(rawPassword, user.getPassword()))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> usersService.check(user, rawPassword))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ===========================
    // 5. 비밀번호 수정
    // ===========================

    @Test
    @DisplayName("비밀번호 수정 성공")
    void updatePassword_success() {
        // given
        Users loginUser = Users.builder()
                .email("test@example.com")
                .password("encoded-current")
                .build();

        UserPasswordUpdateRequest request = UserPasswordUpdateRequest.builder()
                .currentPassword("1234")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        Users dbUser = Users.builder()
                .email("test@example.com")
                .password("encoded-current")
                .build();

        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(dbUser));

        when(passwordEncoder.matches("1234", "encoded-current"))
                .thenReturn(true);

        when(passwordEncoder.encode("5678"))
                .thenReturn("encoded-5678");

        // when
        UserResponse response = usersService.updatePassword(loginUser, request);

        // then
        assertThat(response).isNotNull();
        assertThat(dbUser.getPassword()).isEqualTo("encoded-5678"); // changePassword로 변경되었다고 가정
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 현재 비밀번호 불일치")
    void updatePassword_fail_whenCurrentPasswordWrong() {
        // given
        Users loginUser = Users.builder()
                .email("test@example.com")
                .password("encoded-current")
                .build();

        UserPasswordUpdateRequest request = UserPasswordUpdateRequest.builder()
                .currentPassword("wrong")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        Users dbUser = Users.builder()
                .email("test@example.com")
                .password("encoded-current")
                .build();

        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(dbUser));

        when(passwordEncoder.matches("wrong", "encoded-current"))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> usersService.updatePassword(loginUser, request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 사용자 미존재")
    void updatePassword_fail_whenUserNotFound() {
        // given
        Users loginUser = Users.builder()
                .email("test@example.com")
                .password("encoded-current")
                .build();

        UserPasswordUpdateRequest request = UserPasswordUpdateRequest.builder()
                .currentPassword("1234")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> usersService.updatePassword(loginUser, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===========================
    // 6. 닉네임 수정
    // ===========================

    @Test
    @DisplayName("닉네임 수정 성공")
    void updateNickname_success() {
        // given
        Users loginUser = Users.builder()
                .email("test@example.com")
                .password("encoded-pwd")
                .nickname("oldNick")
                .build();

        UserNicknameUpdateRequest request = UserNicknameUpdateRequest.builder()
                .currentPassword("1234")
                .nickname("newNick")
                .build();

        Users dbUser = Users.builder()
                .email("test@example.com")
                .password("encoded-pwd")
                .nickname("oldNick")
                .build();

        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(dbUser));

        when(passwordEncoder.matches("1234", "encoded-pwd"))
                .thenReturn(true);

        // when
        UserResponse response = usersService.updateNickname(loginUser, request);

        // then
        assertThat(response).isNotNull();
        assertThat(dbUser.getNickname()).isEqualTo("newNick");
    }

    @Test
    @DisplayName("닉네임 수정 실패 - 현재 비밀번호 불일치")
    void updateNickname_fail_whenCurrentPasswordWrong() {
        // given
        Users loginUser = Users.builder()
                .email("test@example.com")
                .password("encoded-pwd")
                .nickname("oldNick")
                .build();

        UserNicknameUpdateRequest request = UserNicknameUpdateRequest.builder()
                .currentPassword("wrong")
                .nickname("newNick")
                .build();

        Users dbUser = Users.builder()
                .email("test@example.com")
                .password("encoded-pwd")
                .nickname("oldNick")
                .build();

        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(dbUser));

        when(passwordEncoder.matches("wrong", "encoded-pwd"))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> usersService.updateNickname(loginUser, request))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ===========================
    // 7. 내 정보 조회
    // ===========================

    @Test
    @DisplayName("getUserInfo - 사용자 존재하면 조회 성공")
    void getUserInfo_success() {
        // given
        // 로그인 유저는 mock으로 만들고, getId()만 우리가 제어
        Users loginUser = mock(Users.class);
        when(loginUser.getId()).thenReturn(1L);

        Users dbUser = Users.builder()
                .email("test@example.com")
                .nickname("piney")
                .build();

        when(usersRepository.findById(1L)).thenReturn(Optional.of(dbUser));

        // when
        UserResponse response = usersService.getUserInfo(loginUser);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("piney");
    }

    @Test
    @DisplayName("getUserInfo - 사용자 미존재 시 ResourceNotFoundException")
    void getUserInfo_fail_whenUserNotFound() {
        // given
        Users loginUser = mock(Users.class);
        when(loginUser.getId()).thenReturn(1L);

        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> usersService.getUserInfo(loginUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===========================
    // 8. 삭제 / 전체 조회
    // ===========================

    @Test
    @DisplayName("delete - deleteById가 호출된다")
    void delete_success() {
        // given
        Long id = 1L;

        // when
        usersService.delete(id);

        // then
        verify(usersRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("getAllUsers - Page<UserResponse>로 변환")
    void getAllUsers_success() {
        // given
        Users u1 = Users.builder()
                .email("a@example.com")
                .nickname("A")
                .build();

        Users u2 = Users.builder()
                .email("b@example.com")
                .nickname("B")
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Users> usersPage = new PageImpl<>(List.of(u1, u2), pageable, 2);

        when(usersRepository.findAll(pageable)).thenReturn(usersPage);

        // when
        Page<UserResponse> result = usersService.getAllUsers(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("a@example.com");
        assertThat(result.getContent().get(1).getEmail()).isEqualTo("b@example.com");
        verify(usersRepository).findAll(pageable);
    }
}