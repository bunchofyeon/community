package com.example.community.dto.response.files;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FileDownloadResponse {

    private String originName;
    private String url;
    private LocalDateTime downloadAt;

}
