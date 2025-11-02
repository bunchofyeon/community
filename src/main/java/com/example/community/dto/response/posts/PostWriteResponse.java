package com.example.community.dto.response.posts;

import com.example.community.entity.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostWriteResponse {

    // 게시글 등록하면 반환할 정보

    private Long id;
    private String title;
    private String content;
    private String nickname;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public PostWriteResponse(Long id, String title, String content, String nickname, Long likeCount, Long commentCount, Long viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostWriteResponse fromEntity(Posts posts) {
        return PostWriteResponse.builder()
                .id(posts.getId())
                .title(posts.getTitle())
                .content(posts.getContent())
                .nickname(posts.getUsers().getNickname())
                .likeCount(posts.getLikeCount())
                .commentCount(posts.getCommentCount())
                .viewCount(posts.getViewCount())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .build();
    }
}
