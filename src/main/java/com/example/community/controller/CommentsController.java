package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.request.comments.CommentRequest;
import com.example.community.dto.response.comments.CommentResponse;
import com.example.community.entity.Users;
import com.example.community.security.jwt.CustomUserDetails;
import com.example.community.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> commentList(
            @PathVariable Long postId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CommentResponse> commentList = commentsService.getAllComments(pageable, postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("댓글 리스트", commentList));
    }

    // 댓글 작성
    @PostMapping("/write")
    public ResponseEntity<ApiResponse<CommentResponse>> write(
            @Valid @RequestBody CommentRequest commentRequest,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users users = customUserDetails.getUsers();
        CommentResponse saveCommentDTO = commentsService.write(postId, users, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글 작성", saveCommentDTO));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Users user = customUserDetails.getUsers();

        CommentResponse updateCommentDTO = commentsService.update(commentId, commentRequest, user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("댓글 수정", updateCommentDTO));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long commentId) {
        commentsService.delete(commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("댓글 삭제", null));
    }

}