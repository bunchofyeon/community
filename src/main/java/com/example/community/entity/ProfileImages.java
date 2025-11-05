package com.example.community.entity;

import com.example.community.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
@Entity
@Getter @NoArgsConstructor

@Table(name = "profile_images")
@SQLDelete(sql = "UPDATE profile_images SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProfileImages extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "original_name")
    private String originalName;

    @Column(nullable = false, name = "image_type", length = 100)
    private String imageType;

    @Column(nullable = false, name = "file_size")
    private Long fileSize;

    @Column(nullable = false, unique = true, name = "storage_key")
    private String storageKey;

    @Column(nullable = false, name = "storage_type")
    private String storageType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 사용자가 있어야 프로필 이미지도 있음
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users users;

    // 연관 편의 메서드
    public void setMappingUser(Users users) {
        this.users = users;
    }

}
