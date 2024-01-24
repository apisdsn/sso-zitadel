package com.demo.spring.upload.file.oauth.sso.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidatorErrorHandler extends RuntimeException {

    private final Map<String, String> error;
    private final int statusCode;

    public ValidatorErrorHandler(HttpStatus httpStatus, String errorCode, String description) {
        super();
        this.error = Map.of("code", errorCode, "description", description);
        this.statusCode = httpStatus.value();
    }

}
