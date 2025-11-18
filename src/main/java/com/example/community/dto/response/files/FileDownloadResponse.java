package com.example.community.dto.response.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDownloadResponse {

    private String downloadUrl; // 이걸로 브라우저에서 바로 GET
    private String fileName;    // 버튼 표시용 파일 이름

    @Builder
    public FileDownloadResponse(String downloadUrl, String fileName) {
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
    }
}
