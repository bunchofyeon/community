package com.example.community.common.exception.custom;

/**
 * 이메일, 닉네임 중복 -> 409 에러
 */

public class ConflictedException extends RuntimeException {

    public ConflictedException(String message) {
        super(message);
    }

}
