package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.request.auth.LoginRequest;
import com.example.community.dto.request.auth.RegisterRequest;
import com.example.community.dto.request.users.UserNicknameUpdateRequest;
import com.example.community.dto.request.users.UserPasswordUpdateRequest;
import com.example.community.dto.request.users.UserUpdateRequest;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final PostsService postsService;
    private final CommentsService commentsService;

    // 회원가입시 이메일 중복 확인
    @GetMapping("/checkEmail")
    public ResponseEntity<?> checkIdDuplicate(@RequestParam String email) {
        usersService.isExistUserEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원가입시 닉네임 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<?> checkNicknameDuplicate(@RequestParam String nickname) {
        usersService.isExistUserNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse successBody = usersService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", successBody));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse successBody = usersService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("로그인 성공", successBody));
    }

    // 로그인 하고나서 비밀번호 일치확인
    @PostMapping("/checkPwd")
    public ResponseEntity<ApiResponse<UserResponse>> check(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody Map<String, String> request) {
        String password = request.get("password");
        Users users = customUserDetails.getUsers();
        UserResponse successBody = usersService.check(users, password);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("비밀번호 일치", successBody));
    }

    // 사용자 정보 수정 - 비밀번호
    @PatchMapping("/updatePassword")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(
            @Valid @RequestBody UserPasswordUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users users = customUserDetails.getUsers();
        UserResponse successBody = usersService.updatePassword(users, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("비밀번호 수정 성공", successBody));
    }

    // 사용자 정보 수정 - 닉네임
    @PatchMapping("/updateNickname")
    public ResponseEntity<ApiResponse<UserResponse>> updateNickname(
            @Valid @RequestBody UserNicknameUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users users = customUserDetails.getUsers();
        UserResponse successBody = usersService.updateNickname(users, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("닉네임 수정 성공", successBody));
    }

    /*
    *
    // 사용자 정보 수정 - 비밀번호
    @PatchMapping ("/update")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users users = customUserDetails.getUsers();
        UserResponse successBody = usersService.update(users, userUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("정보 수정 성공", successBody));
    }

     */

    // 마이페이지 - 사용자별 게시글 조회
    @GetMapping("/myPosts")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> myPosts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Users users = customUserDetails.getUsers();
        Page<PostListResponse> listDTO = postsService.getMyPosts(pageable, users);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("사용자 게시글 목록 조회", listDTO));
    }

    // 마이페이지 - 사용자별 댓글 조회
    @GetMapping("/myComments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> myComments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Users users = customUserDetails.getUsers();
        Page<CommentResponse> listDTO = commentsService.getMyComments(pageable, users);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("사용자 댓글 목록 조회", listDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users me = customUserDetails.getUsers();
        return ResponseEntity.ok(
                ApiResponse.success("me", UserResponse.fromEntity(me))
        );
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMe(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        usersService.delete(customUserDetails.getUsers().getId());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴", null));
    }

    // 관리자가 유저 탈퇴 시킬때 (soft delete 해야되나?)
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long userId) {
        usersService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("회원 탈퇴", null));
    }

}