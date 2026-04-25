package com.saas.metadata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Instant timestamp;
    private Map<String, String> fieldErrors;

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now(), null);
    }

    public static ErrorResponse withFields(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(status, error, message, Instant.now(), fieldErrors);
    }
}
