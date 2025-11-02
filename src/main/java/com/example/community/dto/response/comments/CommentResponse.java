package com.example.community.dto.response.comments;

import com.example.community.common.BaseTimeEntity;
import com.example.community.entity.Comments;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 댓글 조회 응답 DTO
 * 댓글 아이디 / 내용 / 닉네임 / 좋아요 수 / 작성일 / 수정일
 */

@Getter
@NoArgsConstructor
public class CommentResponse extends BaseTimeEntity {

    private Long id;
    private Long postId;
    private String content;
    private String nickname;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CommentResponse(Long id, Long postId, String content, String nickname, Long likeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.nickname = nickname;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity -> DTO
    public static CommentResponse fromEntity(Comments comments) {
        return CommentResponse.builder()
                .id(comments.getId())
                .postId(comments.getPosts().getId()) // 게시글이랑 댓글 연결
                .content(comments.getContent())
                .nickname(comments.getUsers().getNickname()) // Users 엔티티 참조
                .likeCount(comments.getLikeCount())
                .createdAt(comments.getCreatedAt())
                .updatedAt(comments.getUpdatedAt())
                .build();
    }
}
