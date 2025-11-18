package com.example.community.dto.request.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LambdaDownloadRequest {
    private String key; // S3에 저장된 진짜 위치
    private String fileCategory; // 게시글 파일인지 프로필 사진인지 구분
    private String downloadName; // 브라우저에서 보일 파일명 (원본 이름)

    @Builder
    public LambdaDownloadRequest(String key, String fileCategory, String downloadName) {
        this.key = key;
        this.fileCategory = fileCategory;
        this.downloadName = downloadName;
    }
}
