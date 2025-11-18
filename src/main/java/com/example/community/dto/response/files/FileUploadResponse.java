package com.example.community.dto.response.files;

import com.example.community.entity.Files;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FileUploadResponse {

    private Long id;
    private String originName;
    private String fileType;
    private String fileUrl;
    private LocalDateTime uploadedAt;

    @Builder
    public FileUploadResponse(Long id, String originName, String fileType,
                              String fileUrl, LocalDateTime uploadedAt) {
        this.id = id;
        this.originName = originName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
    }

    public static FileUploadResponse fromEntity(Files files) {
        return FileUploadResponse.builder()
                .id(files.getId())
                .originName(files.getOriginName())
                .fileType(files.getFileType())
                .fileUrl(files.getFileUrl())
                .uploadedAt(files.getCreatedAt())
                .build();
    }
}
