package com.example.community.dto.response.files;

import com.example.community.entity.Files;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FilePostDetailsResponse {

    private Long id;
    private String originName;
    private String fileType;
    private String url;
    private LocalDateTime uploadedAt;

    @Builder
    public FilePostDetailsResponse(Long id, String originName, String fileType, String url, LocalDateTime uploadedAt) {
        this.id = id;
        this.originName = originName;
        this.fileType = fileType;
        this.url = url;
        this.uploadedAt = uploadedAt;
    }

    public static FilePostDetailsResponse fromEntity(Files files) {
        return FilePostDetailsResponse.builder()
                .id(files.getId())
                .originName(files.getOriginName())
                .fileType(files.getFileType())
                .url("/api/files/" + files.getId() + "/download")
                .build();
    }

}
