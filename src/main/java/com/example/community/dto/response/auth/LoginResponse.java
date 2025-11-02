package com.example.community.dto.response.auth;

import com.example.community.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String email;
    private String token;

    @Builder
    public LoginResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }

    // Entity -> DTO
    public static LoginResponse fromEntity(Users users, String token) {
        return LoginResponse.builder()
                .email(users.getEmail())
                .token(token)
                .build();
    }

}
