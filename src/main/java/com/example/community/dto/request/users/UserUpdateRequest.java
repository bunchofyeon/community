package com.example.community.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "변경할 비밀번호가 일치하지 않습니다.")
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max=10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;

    private String profileImageUrl;

    @Builder
    public UserUpdateRequest(String email, String currentPassword, String password, String passwordCheck, String nickname, String profileImageUrl) {
        this.email = email;
        this.currentPassword = currentPassword;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
