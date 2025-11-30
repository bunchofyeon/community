package com.example.community.service;

import com.example.community.common.exception.custom.BadRequestException;
import com.example.community.common.exception.custom.ConflictedException;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.response.auth.RegisterResponse;
import com.example.community.entity.Users;
import com.example.community.repository.UsersRepository;
import com.example.community.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void duplicatedEmail() {
        // given
        String email = "test@example.com";
        when(usersRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> usersService.isExistUserEmail(email))
                .isInstanceOf(ConflictedException.class);

        verify(usersRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 아님 - 통과")
    void notDuplicatedEmail() {
        // given
        String email = "test@example.com";
        when(usersRepository.existsByEmail(email)).thenReturn(false);

        // when & then
        assertThatCode(() -> usersService.isExistUserEmail(email))
                .doesNotThrowAnyException();
        verify(usersRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("닉네임 중복 - ConflictedException")
    void duplicatedNickname() {
        // given
        String nickname = "piney";
        when(usersRepository.existsByNickname(nickname)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> usersService.isExistUserNickname(nickname))
                .isInstanceOf(ConflictedException.class);

        verify(usersRepository, times(1)).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 아님 - 통과")
    void notDuplicatedNickname() {
        // given
        String nickname = "piney";
        when(usersRepository.existsByNickname(nickname)).thenReturn(false);

        // when & then
        assertThatCode(() -> usersService.isExistUserNickname(nickname))
                .doesNotThrowAnyException();
        verify(usersRepository, times(1)).existsByNickname(nickname);
    }

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

        when(passwordEncoder.encode("1234")).thenReturn("1234");

        Users savedUser = Users.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password("1234")
                .build();

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
    void register_fail_duplicatedEmail() {
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
    void register_fail_passwordNotMatch() {
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

}