package com.example.community.common.response;

import lombok.Getter;

/**
 * API 처리 클래스
 */

@Getter
public class ApiResponse<T> {

    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(message, null);
    }

}
