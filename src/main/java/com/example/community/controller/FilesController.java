package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.response.files.FileDownloadResponse;
import com.example.community.dto.response.files.FileUploadResponse;
import com.example.community.entity.Users;
import com.example.community.security.jwt.CustomUserDetails;
import com.example.community.service.FilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/files")
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;

    /// 업로드 (게시글에 파일 추가)
    @PostMapping
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadPostFile(
            @PathVariable Long postId,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {

        Users user = customUserDetails.getUsers();

        List<FileUploadResponse> response = filesService.upload(files, postId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("파일 업로드 성공", response));
    }

    /// 다운로드 URL 발급
    @GetMapping("/{fileId}/download-url")
    public ResponseEntity<ApiResponse<FileDownloadResponse>> getDownloadUrl(
            @PathVariable Long postId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Users user = customUserDetails.getUsers();

        FileDownloadResponse response = filesService.getDownloadUrl(fileId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("파일 다운로드 URL 발급 성공", response));
    }

    /// 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> fileList(
            @PathVariable Long postId
    ) throws Exception {
        List<FileUploadResponse> response = filesService.getAllFiles(postId);
        return ResponseEntity.ok(
                ApiResponse.success("파일 조회 성공", response)
        );
    }

    /// 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long postId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Users user = customUserDetails.getUsers();
        filesService.deleteFile(fileId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("파일 삭제 성공", null));
    }

    /// 교체 - (그냥 삭제하고 다시 업로드하는 방식으로)
    @PatchMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileUploadResponse>> replace(
            @PathVariable Long postId,
            @PathVariable Long fileId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {

        Users user = customUserDetails.getUsers();

        FileUploadResponse response = filesService.replaceFile(fileId, file, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("파일 교체(수정) 성공", response));
    }
}