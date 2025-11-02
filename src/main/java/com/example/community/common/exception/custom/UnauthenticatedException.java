package com.example.community.common.exception.custom;

/**
 *  403 Forbidden
 */

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String message) {
        super(message);
    }

}
