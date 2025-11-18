package com.example.community.service;

import com.example.community.common.exception.custom.UnauthenticatedException;
import com.example.community.dto.request.files.LambdaDownloadRequest;
import com.example.community.dto.request.files.LambdaUploadRequest;
import com.example.community.dto.response.files.FileDownloadResponse;
import com.example.community.dto.response.files.FileUploadResponse;
import com.example.community.dto.response.files.LambdaDownloadResponse;
import com.example.community.dto.response.files.LambdaUploadResponse;
import com.example.community.dto.response.posts.PostListResponse;
import com.example.community.entity.Files;
import com.example.community.entity.Posts;
import com.example.community.entity.Users;
import com.example.community.repository.FilesRepository;
import com.example.community.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilesService {

    private final PostsRepository postsRepository;
    private final FilesRepository filesRepository;
    private final RestTemplate restTemplate;

    @Value("${lambda.file.endpoint}")
    private String lambdaEndpoint;

    /// 업로드
    // 파일 여러 장 업로드
    public List<FileUploadResponse> upload(List<MultipartFile> files, Long postId, Users users) throws Exception {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 게시글 조회
        Posts posts = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 권한 체크 (작성자만 업로드 가능)
        if (!posts.getUsers().getId().equals(users.getId())) {
            throw new UnauthenticatedException("업로드 권한이 없습니다.");
        }

        List<FileUploadResponse> result = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            result.add(uploadSingleFile(file, posts));
        }

        return result;
    }

    // 파일 한 장 업로드 (내부용)
    private FileUploadResponse uploadSingleFile(MultipartFile file, Posts posts) throws Exception {

        String originalFilename = file.getOriginalFilename();
        String ext = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // S3 key 생성
        String key = "posts/" + posts.getId() + "/" + UUID.randomUUID() + ext;

        // Lambda 요청 DTO
        LambdaUploadRequest lambdaReq = new LambdaUploadRequest(
                key,
                file.getContentType(),
                file.getSize(),
                String.valueOf(posts.getUsers().getId()), // uploaderId
                posts.getId(),
                "post-file"
        );

        String url = lambdaEndpoint + "/files";

        // Lambda 호출 → Presigned PUT URL 받기
        LambdaUploadResponse lambdaRes =
                restTemplate.postForObject(url, lambdaReq, LambdaUploadResponse.class);

        if (lambdaRes == null || lambdaRes.getUploadUrl() == null) {
            log.error("[FILE-UPLOAD] Lambda 응답 오류: {}", lambdaRes);
            throw new IllegalStateException("Lambda에서 업로드 URL을 받지 못했습니다.");
        }

        // Presigned URL로 S3에 실제 파일 업로드
        uploadToPresignedUrl(lambdaRes.getUploadUrl(), file);

        // DB에 파일 메타데이터 저장
        Files saved = filesRepository.save(
                Files.builder()
                        .originName(originalFilename)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .s3Key(key)
                        .fileUrl(lambdaRes.getFileUrl())
                        .posts(posts)
                        .deletedAt(null)
                        .build()
        );

        return FileUploadResponse.fromEntity(saved);
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

    /// 게시글 목록 조회
    public List<FileUploadResponse> getAllFiles(Long postId) throws Exception {
        List<Files> files = filesRepository.findByPostsId(postId);
        return files.stream()
                .map(FileUploadResponse::fromEntity)
                .toList();
    }

    /// 다운로드 (presigned GET URL 발급)
    public FileDownloadResponse getDownloadUrl(Long fileId, Users currentUser) {

        log.info("[FILE-DOWNLOAD] 요청: fileId={}, userId={}",
                fileId, currentUser != null ? currentUser.getId() : null);

        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // Lambda에 보낼 요청 (Download용)
        LambdaDownloadRequest lambdaReq = new LambdaDownloadRequest(
                file.getS3Key(),
                "post-file",
                file.getOriginName()
        );

        String url = lambdaEndpoint + "/files/download";

        LambdaDownloadResponse lambdaRes =
                restTemplate.postForObject(url, lambdaReq, LambdaDownloadResponse.class);

        if (lambdaRes == null || lambdaRes.getDownloadUrl() == null) {
            log.error("[FILE-DOWNLOAD] Lambda 응답 오류: {}", lambdaRes);
            throw new IllegalStateException("Lambda에서 다운로드 URL을 받지 못했습니다.");
        }

        log.info("[FILE-DOWNLOAD] presigned URL 발급 완료: fileId={}, expires={}s",
                fileId, lambdaRes.getExpireSeconds());

        return new FileDownloadResponse(
                lambdaRes.getDownloadUrl(),
                file.getOriginName()
        );
    }

    ///  삭제
    public void deleteFile(Long fileId, Users currentUser) {

        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        Posts post = file.getPosts();

        // 권한 체크
        if (post != null) {
            Long ownerId = post.getUsers().getId();
            if (!ownerId.equals(currentUser.getId())) {
                throw new UnauthenticatedException("삭제 권한이 없습니다.");
            }
        }

        // 현재는 DB에서만 soft delete 처리함
        // S3 객체 제거도 해야하는데..
        filesRepository.delete(file);
    }

    /// 수정 (기존 파일 삭제 + 새 파일 업로드)
    public FileUploadResponse replaceFile(Long fileId, MultipartFile newFile, Users currentUser) throws Exception {

        if (newFile == null || newFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 새 파일이 없습니다.");
        }

        Files oldFile = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        Posts post = oldFile.getPosts();

        // 권한 체크
        if (post != null) {
            Long ownerId = post.getUsers().getId();
            if (!ownerId.equals(currentUser.getId())) {
                throw new UnauthenticatedException("수정 권한이 없습니다.");
            }
        }
        filesRepository.delete(oldFile);

        // 같은 게시글에 대해 새 파일 업로드
        return uploadSingleFile(newFile, post);
    }
}