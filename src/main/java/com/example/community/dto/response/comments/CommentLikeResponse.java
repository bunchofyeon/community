package com.example.community.dto.response.comments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 좋아요 응답 DTO
 */

@Getter
@NoArgsConstructor
public class CommentLikeResponse {

    private Long commentId; // 어떤 댓글에 대한 응답인지
    private Long likeCount;
    private boolean liked; // DB에는 저장하지 않음, 프론트에서 상태 표시용(사용자가 좋아요 중인지 여부)

    @Builder
    public CommentLikeResponse(Long commentId, Long likeCount, boolean liked) {
        this.commentId = commentId;
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public static CommentLikeResponse of(Long commentId, Long likeCount, boolean liked) {
        return CommentLikeResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount)
                .liked(liked)
                .build();
    }

}
