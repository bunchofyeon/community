package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.response.auth.RegisterResponse;
import com.example.community.service.CommentsService;
import com.example.community.service.PostsService;
import com.example.community.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PostsService postsService;

    @Mock
    private CommentsService commentsService;

    @InjectMocks
    private UsersController usersController;

    @Test
    @DisplayName("이메일 중복 확인 - 성공")
    void checkIdDuplicate_success() {
        // given
        String email = "test@example.com";

        // when
        ResponseEntity<?> response = usersController.checkIdDuplicate(email);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(usersService, times(1)).isExistUserEmail(email);
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 성공")
    void checkNicknameDuplicate_success() {
        // given
        String nickname = "piney";

        // when
        ResponseEntity<?> response = usersController.checkNicknameDuplicate(nickname);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(usersService, times(1)).isExistUserNickname(nickname);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void register_success() {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .nickname("piney")
                .password("1234")
                .passwordCheck("1234")
                .build();

        RegisterResponse registerResponse = RegisterResponse.builder()
                .email("test@example.com")
                .nickname("piney")
                .build();

        when(usersService.register(request)).thenReturn(registerResponse);

        // when
        ResponseEntity<ApiResponse<RegisterResponse>> response = usersController.register(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("회원가입 성공");
        assertThat(response.getBody().getData()).isEqualTo(registerResponse);
        verify(usersService, times(1)).register(request);
    }
}