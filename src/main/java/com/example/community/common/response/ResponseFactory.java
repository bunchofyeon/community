package com.example.community.common.response;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ResponseFactory {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success("success", data)); // ok로 바꿀까 말까 ㄷㄷ
    }

    // 자원이 생성되는 API 는 response 에 URI 를 추가해주는게 좋음
    public static <T> ResponseEntity<ApiResponse<T>> created(URI location, T data) {
        return ResponseEntity.created(location).body(ApiResponse.success("created", data));
    }
}
