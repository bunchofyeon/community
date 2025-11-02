package com.example.community.dto.response.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 게시글 좋아요 응답 DTO
 * Posts나 PostLikes 엔티티 자체를 안 넘기고
 * 단순히 postId, count, liked 여부 값만으로 응답 DTO 만들기
 */

@Getter
@NoArgsConstructor
public class PostLikeResponse {

    private Long postId; // 어떤 게시물에 대한 응답인지
    private Long likeCount;
    private boolean liked; // DB에 저장 안하고 프론트에 좋아요 여부 알려주기

    @Builder
    public PostLikeResponse(Long postId, Long likeCount, boolean liked) {
        this.postId = postId;
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public static PostLikeResponse of(Long postId, Long likeCount, boolean liked) {
        return PostLikeResponse.builder()
                .postId(postId)
                .likeCount(likeCount)
                .liked(liked)
                .build();
    }
}
