/*
package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.common.response.ResponseFactory;
import com.example.community.dto.request.posts.PostWriteRequest;
import com.example.community.dto.response.files.FileUploadResponse;
import com.example.community.dto.response.posts.PostWriteResponse;
import com.example.community.entity.Users;
import com.example.community.security.jwt.CustomUserDetails;
import com.example.community.service.FilesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/files")
@RequiredArgsConstructor
@Slf4j
public class FilesController {

    private final FilesService filesService;

    // 근데 어차피 매서드가 다 다른데 꼭 뒤에 /upload같은거 적어줘야함??
    // 1. 파일 업로드 (여러개도 가능하도록)
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> write(
            @PathVariable Long postId,
            @RequestParam("file") List<MultipartFile> files) {
        List<FileUploadResponse> uploadFile = filesService.upload(postId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("파일 업로드", uploadFile));
    }

    // 2. 파일 다운로드
    @GetMapping("download")

    // 3. 파일 삭제
    @DeleteMapping("delete")

}

 */
