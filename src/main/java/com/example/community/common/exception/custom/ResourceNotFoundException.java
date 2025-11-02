package com.example.community.common.exception.custom;

/**
 *  이메일, 비밀번호 오류 (400)
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
