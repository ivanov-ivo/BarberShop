package com.example.barbershop.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final String timestamp;
    private final String path;
    private final Map<String, String> details;

    public ErrorResponse(String errorCode, String message, String path, Map<String, String> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
        this.path = path;
        // Defensive copy to prevent exposure of internal representation
        this.details = details != null ? new HashMap<>(details) : Collections.emptyMap();
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    // Defensive copy to prevent exposure of internal representation
    public Map<String, String> getDetails() {
        return details != null ? new HashMap<>(details) : Collections.emptyMap();
    }
}
