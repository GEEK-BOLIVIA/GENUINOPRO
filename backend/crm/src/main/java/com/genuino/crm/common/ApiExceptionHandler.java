package com.genuino.crm.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of(
                "error", "ILLEGAL_STATE",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleSecurity(SecurityException ex) {
        return Map.of(
                "error", "FORBIDDEN",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneric(Exception ex) {
        return Map.of(
                "error", "INTERNAL_ERROR",
                "type", ex.getClass().getSimpleName(),
                "message", ex.getMessage()
        );
    }
}