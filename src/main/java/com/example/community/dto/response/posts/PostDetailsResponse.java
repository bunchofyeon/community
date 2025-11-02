package com.example.community.dto.response.posts;


import com.example.community.entity.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 상세 조회
 * 게시글 아이디 / 제목 / 내용 / 닉네임 / 조회수 / 좋아요수 / 댓글수 / 작성일 / 수정일
 */

@Getter
@NoArgsConstructor
public class PostDetailsResponse {

    private Long id;
    private String title;
    private String content;
    private String nickname;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public PostDetailsResponse(Long id, String title, String content, String nickname, Long viewCount, Long likeCount, Long commentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity -> DTO
    public static PostDetailsResponse fromEntity(Posts posts) {
        return PostDetailsResponse.builder()
                .id(posts.getId())
                .title(posts.getTitle())
                .content(posts.getContent())
                .nickname(posts.getUsers().getNickname())
                .viewCount(posts.getViewCount())
                .likeCount(posts.getLikeCount())
                .commentCount(posts.getCommentCount())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .build();
    }

}
