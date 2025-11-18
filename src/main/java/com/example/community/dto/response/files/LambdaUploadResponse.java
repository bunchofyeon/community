package com.example.community.dto.response.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LambdaUploadResponse {

    private String uploadUrl;     // PUT Presigned URL (여기로 실제 파일 업로드)
    private String fileUrl;       // 최종 접근 URL (CloudFront or S3 URL)
    private String key;           // S3 key
    private Long expireSeconds;   // Presigned URL 만료 시간(초)

    @Builder
    public LambdaUploadResponse(String uploadUrl, String fileUrl, String key, Long expireSeconds) {
        this.uploadUrl = uploadUrl;
        this.fileUrl = fileUrl;
        this.key = key;
        this.expireSeconds = expireSeconds;
    }

}
