package com.example.community.entity;

import com.example.community.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE files SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Files extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "origin_name")
    private String originName; // 업로드 당시 원본 파일명

    @Column(nullable = false, name = "file_type")
    private String fileType;

    @Column(nullable = false, name = "file_size")
    private Long fileSize;

    @Column(nullable = false, name = "s3_key")
    private String s3Key;

    @Column(nullable = false, name = "file_url")
    private String fileUrl;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts posts;

    @Builder
    public Files(String originName, String fileType, Long fileSize,
                 String s3Key, String fileUrl, LocalDateTime deletedAt, Posts posts) {
        this.originName = originName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.s3Key = s3Key;
        this.fileUrl = fileUrl;
        this.deletedAt = deletedAt;
        this.posts = posts;
    }

    public void setMappingPosts(Posts posts) {
        this.posts = posts;
    }

}
