package com.example.community.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPasswordUpdateRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
    @Size(min = 4, max = 8)
    private String newPassword;

    @NotBlank(message = "변경할 비밀번호가 일치하지 않습니다.")
    private String newPasswordCheck;

    @Builder
    public UserPasswordUpdateRequest(String currentPassword, String newPassword, String newPasswordCheck) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }
}
