package com.example.community.dto.response.auth;

import com.example.community.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterResponse {

    // 회원가입하면 뭘 보여줄지 너무 고민됐다...
    private String email;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public RegisterResponse(String email, String nickname, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    // Entity -> DTO
    public static RegisterResponse fromEntity(Users users) {
        return RegisterResponse.builder()
                .email(users.getEmail())
                .nickname(users.getNickname())
                .profileImageUrl(users.getProfileImageUrl())
                .build();
    }
}
