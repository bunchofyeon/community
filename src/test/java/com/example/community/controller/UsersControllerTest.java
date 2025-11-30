package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.request.auth.LoginRequest;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.request.users.UserNicknameUpdateRequest;
import com.example.community.dto.request.users.UserPasswordUpdateRequest;
import com.example.community.dto.response.auth.LoginResponse;
import com.example.community.dto.response.auth.RegisterResponse;
import com.example.community.dto.response.comments.CommentResponse;
import com.example.community.dto.response.posts.PostListResponse;
import com.example.community.dto.response.users.UserResponse;
import com.example.community.entity.Users;
import com.example.community.security.jwt.CustomUserDetails;
import com.example.community.service.CommentsService;
import com.example.community.service.PostsService;
import com.example.community.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

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

    // ===========================
    // 1. 이메일 / 닉네임 중복 확인
    // ===========================

    @Test
    @DisplayName("이메일 중복 확인 - 성공 (예외 없음, 200 OK)")
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
    @DisplayName("닉네임 중복 확인 - 성공 (예외 없음, 200 OK)")
    void checkNicknameDuplicate_success() {
        // given
        String nickname = "piney";

        // when
        ResponseEntity<?> response = usersController.checkNicknameDuplicate(nickname);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(usersService, times(1)).isExistUserNickname(nickname);
    }

    // ===========================
    // 2. 회원가입 / 로그인
    // ===========================

    @Test
    @DisplayName("회원가입 - 201 CREATED + ApiResponse")
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

    @Test
    @DisplayName("로그인 - 201 CREATED + ApiResponse")
    void login_success() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("1234")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .email("test@example.com")
                .token("fake-token")
                .build();

        when(usersService.login(request)).thenReturn(loginResponse);

        // when
        ResponseEntity<ApiResponse<LoginResponse>> response = usersController.login(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("로그인 성공");
        assertThat(response.getBody().getData()).isEqualTo(loginResponse);
        verify(usersService, times(1)).login(request);
    }

    // ===========================
    // 3. 비밀번호 확인 / 수정, 닉네임 수정
    // ===========================

    @Test
    @DisplayName("비밀번호 일치 확인 - 201 CREATED + ApiResponse")
    void check_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        String password = "1234";
        Map<String, String> requestMap = Map.of("password", password);

        UserResponse userResponse = UserResponse.builder()
                .email("test@example.com")
                .nickname("piney")
                .build();

        when(usersService.check(user, password)).thenReturn(userResponse);

        // when
        ResponseEntity<ApiResponse<UserResponse>> response = usersController.check(customUserDetails, requestMap);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("비밀번호 일치");
        assertThat(response.getBody().getData()).isEqualTo(userResponse);
        verify(usersService, times(1)).check(user, password);
    }

    @Test
    @DisplayName("비밀번호 수정 - 200 OK + ApiResponse")
    void updatePassword_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        UserPasswordUpdateRequest request = UserPasswordUpdateRequest.builder()
                .currentPassword("1234")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("test@example.com")
                .nickname("piney")
                .build();

        when(usersService.updatePassword(user, request)).thenReturn(userResponse);

        // when
        ResponseEntity<ApiResponse<UserResponse>> response = usersController.updatePassword(request, customUserDetails);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("비밀번호 수정 성공");
        assertThat(response.getBody().getData()).isEqualTo(userResponse);
        verify(usersService, times(1)).updatePassword(user, request);
    }

    @Test
    @DisplayName("닉네임 수정 - 200 OK + ApiResponse")
    void updateNickname_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        UserNicknameUpdateRequest request = UserNicknameUpdateRequest.builder()
                .currentPassword("1234")
                .nickname("newNick")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("test@example.com")
                .nickname("newNick")
                .build();

        when(usersService.updateNickname(user, request)).thenReturn(userResponse);

        // when
        ResponseEntity<ApiResponse<UserResponse>> response = usersController.updateNickname(request, customUserDetails);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("닉네임 수정 성공");
        assertThat(response.getBody().getData()).isEqualTo(userResponse);
        verify(usersService, times(1)).updateNickname(user, request);
    }

    // ===========================
    // 4. 마이페이지 - 내 게시글 / 내 댓글 / 내 정보
    // ===========================

    @Test
    @DisplayName("마이페이지 - 내 게시글 조회 - 200 OK")
    void myPosts_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 10);

        PostListResponse post = PostListResponse.builder()
                .id(1L)
                .title("title")
                .build();

        Page<PostListResponse> page = new PageImpl<>(List.of(post), pageable, 1);
        when(postsService.getMyPosts(pageable, user)).thenReturn(page);

        // when
        ResponseEntity<ApiResponse<Page<PostListResponse>>> response = usersController.myPosts(customUserDetails, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(page);
        verify(postsService, times(1)).getMyPosts(pageable, user);
    }

    @Test
    @DisplayName("마이페이지 - 내 댓글 조회 - 200 OK")
    void myComments_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 10);

        CommentResponse comment = CommentResponse.builder()
                .id(1L)
                .content("comment")
                .build();

        Page<CommentResponse> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentsService.getMyComments(pageable, user)).thenReturn(page);

        // when
        ResponseEntity<ApiResponse<Page<CommentResponse>>> response = usersController.myComments(customUserDetails, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(page);
        verify(commentsService, times(1)).getMyComments(pageable, user);
    }

    @Test
    @DisplayName("내 정보 조회(me) - 200 OK + ApiResponse")
    void me_success() {
        // given
        Users user = mock(Users.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        UserResponse userResponse = UserResponse.builder()
                .email("test@example.com")
                .nickname("piney")
                .build();

        when(usersService.getUserInfo(user)).thenReturn(userResponse);

        // when
        ResponseEntity<ApiResponse<UserResponse>> response = usersController.me(customUserDetails);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("사용자 정보 조회 성공");
        assertThat(response.getBody().getData()).isEqualTo(userResponse);
        verify(usersService, times(1)).getUserInfo(user);
    }

    // ===========================
    // 5. 회원 탈퇴 (me, 관리자용)
    // ===========================

    @Test
    @DisplayName("회원 탈퇴 - 본인(me) 삭제 - 200 OK")
    void deleteMe_success() {
        // given
        Users user = mock(Users.class);
        when(user.getId()).thenReturn(1L);

        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUsers()).thenReturn(user);

        // when
        ResponseEntity<ApiResponse<Void>> response = usersController.deleteMe(customUserDetails);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("회원 탈퇴");
        verify(usersService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("회원 탈퇴 - 관리자에 의한 삭제 - 200 OK")
    void delete_success() {
        // given
        Long userId = 1L;

        // when
        ResponseEntity<ApiResponse<Void>> response = usersController.delete(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("회원 탈퇴");
        verify(usersService, times(1)).delete(userId);
    }
}