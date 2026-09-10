package com.zimgo.colog.exception;

import org.springframework.http.HttpStatus;

public final class RequestValidator {

    private RequestValidator() {
    }

    public static <T> T requireBody(T request) {
        if (request == null) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "REQUEST_BODY_REQUIRED",
                    "Request body is required"
            );
        }
        return request;
    }
}
