package com.example.community.dto.request.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Spring -> Lambda
 */

@Getter
@NoArgsConstructor
public class LambdaUploadRequest {

    private String key;          // S3에 저장할 경로
    private String contentType;  // MIME 타입
    private Long contentSize;  // 파일 크기
    private String uploaderId;   // 업로드한 사용자
    private Long postId;         // 어떤 게시글에 속하는지 (없으면 null)
    private String fileCategory; // "profile", "post-file" 등 구분용

    @Builder
    public LambdaUploadRequest(String key, String contentType, Long contentSize,
                               String uploaderId, Long postId, String fileCategory) {
        this.key = key;
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.uploaderId = uploaderId;
        this.postId = postId;
        this.fileCategory = fileCategory;
    }
}
