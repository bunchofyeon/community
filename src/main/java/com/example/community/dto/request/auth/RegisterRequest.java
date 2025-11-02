package com.example.community.dto.request.auth;

import com.example.community.entity.Role;
import com.example.community.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max=10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;

    private String profileImageUrl;

    @Builder
    public RegisterRequest(String email, String password, String passwordCheck, String nickname, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    // DTO -> Entity
    public static Users ofEntity(RegisterRequest dto) {
        return Users.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .profileImageUrl(dto.getProfileImageUrl())
                .role(Role.USER)
                .build();
    }

}
