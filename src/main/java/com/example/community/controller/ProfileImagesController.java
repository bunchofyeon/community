package com.example.community.controller;

import com.example.community.common.response.ApiResponse;
import com.example.community.dto.response.files.ProfileImageResponse;
import com.example.community.security.jwt.CustomUserDetails;
import com.example.community.service.ProfileImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile-image")
@RequiredArgsConstructor
public class ProfileImagesController {

    private final ProfileImagesService profileImagesService;

    /// 업로드
    @PostMapping
    public ResponseEntity<ApiResponse<ProfileImageResponse>> uploadProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {

        Long userId = customUserDetails.getUsers().getId();

        ProfileImageResponse response =
                profileImagesService.uploadProfileImage(file, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 이미지 업로드 성공", response));
    }

    /// 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Long userId = customUserDetails.getUsers().getId();
        profileImagesService.deleteProfileImage(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 이미지 삭제 성공", null));
    }

    /// 수정(교체)
    @PatchMapping
    public ResponseEntity<ApiResponse<ProfileImageResponse>> updateProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {

        Long userId = customUserDetails.getUsers().getId();

        ProfileImageResponse response =
                profileImagesService.uploadProfileImage(file, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 이미지 수정 성공", response));
    }
}