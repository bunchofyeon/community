package com.example.community.dto.response.files;

import com.example.community.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileImageResponse {

    private String profileImageUrl;

    @Builder
    public ProfileImageResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static ProfileImageResponse fromEntity(Users user) {
        return ProfileImageResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
