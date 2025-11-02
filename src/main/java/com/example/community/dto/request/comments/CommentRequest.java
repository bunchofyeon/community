package com.example.community.dto.request.comments;

import com.example.community.entity.Comments;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public CommentRequest(String content) {
        this.content = content;
    }

    public static Comments ofEntity(CommentRequest dto) {
        return Comments.builder()
                .content(dto.getContent())
                .build();
    }
}
