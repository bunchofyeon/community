package com.example.community.entity;

import com.example.community.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 10, unique = true)
    private String nickname;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImages profileImages;

    @Builder
    public Users(String email, String password, String nickname, String profileImageUrl, LocalDateTime lastLoginAt, LocalDateTime deletedAt, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.lastLoginAt = lastLoginAt;
        this.deletedAt = deletedAt;
        this.role = role;
    }

    public void update(String password, String nickname, String profileImageUrl) {
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfileImage(String profileImageKey, String profileImageUrl) {
        this.profileImageKey = profileImageKey;
        this.profileImageUrl = profileImageUrl;
    }

    public void removeProfileImage(String profileImageKey, String profileImageUrl) {
        this.profileImageKey = null;
        this.profileImageUrl = null;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
}
