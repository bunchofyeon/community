package com.example.community.common.exception.custom;

/**
 *  권한 없을 때 (401)
 */

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

}
