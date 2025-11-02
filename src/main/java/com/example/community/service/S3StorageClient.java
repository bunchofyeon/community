package com.example.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageClient {

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${app.storage.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${app.storage.public:true}")
    private boolean isPublic;

    @Value("${app.storage.cdn-base-url:}")
    private String cdnBaseUrl;

    public String upload(String key, MultipartFile file) throws IOException {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return key;
    }

    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    public String publicUrl(String key) {
        if (!cdnBaseUrl.isBlank()) return cdnBaseUrl + "/" + key;
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    public String presignedGetUrl(String key, Duration ttl) {
        var req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        var pre = presigner.presignGetObject(b -> b.signatureDuration(ttl).getObjectRequest(req));
        return pre.url().toString();
    }
}