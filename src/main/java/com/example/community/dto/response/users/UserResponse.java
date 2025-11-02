package com.example.community.dto.response.users;

import com.example.community.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String role;

    @Builder
    public UserResponse(Long id, String email, String nickname, String profileImageUrl, String role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    // Entity -> DTO 변환
    public static UserResponse fromEntity(Users users) {
        return UserResponse.builder()
                .id(users.getId())
                .email(users.getEmail())
                .nickname(users.getNickname())
                .profileImageUrl(users.getProfileImageUrl())
                .role(users.getRole() != null ? users.getRole().name() : null) // 흠...
                .build();
    }
}