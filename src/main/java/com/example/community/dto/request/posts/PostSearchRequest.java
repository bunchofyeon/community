package com.example.community.dto.request.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class  PostSearchRequest {

    private String title;
    private String content;
    private String nickname;

    @Builder
    public PostSearchRequest(String title, String content, String nickname) {
        this.title = title;
        this.content = content;
        this.nickname = nickname;
    }

    public static PostSearchRequest postSearchRequest(String title, String content, String nickname) {
        return PostSearchRequest.builder()
                .title(title)
                .content(content)
                .nickname(nickname).
                build();
    }

}
