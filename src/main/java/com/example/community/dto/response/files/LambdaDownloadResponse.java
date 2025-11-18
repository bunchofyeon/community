package com.example.community.dto.response.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LambdaDownloadResponse {

    private String downloadUrl;
    private String key;
    private Long expireSeconds;

    @Builder
    public LambdaDownloadResponse(String downloadUrl, String key, Long expireSeconds) {
        this.downloadUrl = downloadUrl;
        this.key = key;
        this.expireSeconds = expireSeconds;
    }
}
