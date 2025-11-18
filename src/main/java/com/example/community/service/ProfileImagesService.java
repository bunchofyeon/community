package com.example.community.service;

import com.example.community.dto.request.files.LambdaUploadRequest;
import com.example.community.dto.response.files.LambdaUploadResponse;
import com.example.community.dto.response.files.ProfileImageResponse;
import com.example.community.entity.Users;
import com.example.community.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfileImagesService {

    private final UsersRepository usersRepository;
    private final RestTemplate restTemplate;

    @Value("${lambda.file.endpoint}")
    private String lambdaEndpoint;

    /**
     * 프로필 이미지 업로드 (한 장)
     * - 새로 업로드할 때도 쓰고,
     * - 기존 이미지가 있다면 덮어쓰기 용도로도 그대로 사용 가능
     */
    public ProfileImageResponse uploadProfileImage(MultipartFile file, Long userId) throws Exception {

        validateImage(file);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 이미지가 있다면 로깅만 (S3 삭제는 TODO)
        String oldKey = user.getProfileImageKey();
        if (oldKey != null) {
            log.info("[PROFILE-IMAGE] 기존 프로필 이미지 존재: userId={}, oldKey={}", userId, oldKey);
            // TODO: 기존 프로필 이미지 S3 삭제가 필요하면 여기서 처리
        }

        // 2. S3 key 생성 (profiles/{userId}/uuid.ext)
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String key = "profiles/" + user.getId() + "/" + UUID.randomUUID() + ext;

        // 3. Lambda에 Presigned URL 요청
        LambdaUploadRequest lambdaReq = new LambdaUploadRequest(
                key,
                file.getContentType(),
                file.getSize(),
                String.valueOf(user.getId()),   // uploaderId
                null,                           // postId 없음
                "profile"                       // fileCategory
        );

        String url = lambdaEndpoint + "/files";

        LambdaUploadResponse lambdaRes =
                restTemplate.postForObject(url, lambdaReq, LambdaUploadResponse.class);

        if (lambdaRes == null || lambdaRes.getUploadUrl() == null) {
            throw new IllegalStateException("Lambda에서 업로드 URL을 받지 못했습니다.");
        }

        // 4. Presigned URL로 S3 업로드 (PUT)
        uploadToPresignedUrl(lambdaRes.getUploadUrl(), file);

        // 5. Users 엔티티에 프로필 이미지 정보 저장 (교체 포함)
        user.updateProfileImage(key, lambdaRes.getFileUrl());

        return ProfileImageResponse.fromEntity(user);
    }

    /**
     * 프로필 이미지 삭제
     */
    public void deleteProfileImage(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String oldKey = user.getProfileImageKey();
        if (oldKey != null) {
            log.info("[PROFILE-IMAGE] 프로필 삭제 요청: userId={}, oldKey={}", userId, oldKey);
            // TODO: S3 프로필 이미지 삭제 필요하면 여기서 처리
        }

        user.removeProfileImage(null, null); // 내부에서 key/url을 null로 셋팅
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    private void uploadToPresignedUrl(String uploadUrl, MultipartFile file) throws Exception {
        URL url = new URL(uploadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", file.getContentType());

        try (var os = conn.getOutputStream()) {
            os.write(file.getBytes());
        }

        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("S3 업로드 실패: HTTP " + code);
        }
    }
}