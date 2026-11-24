package com.fedspending.contracts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fedspending.contracts.client.UsaSpendingClientException;
import com.fedspending.contracts.model.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Invalid request", details);
    }

    @ExceptionHandler(UsaSpendingClientException.class)
    ResponseEntity<ErrorResponse> handleUpstream(UsaSpendingClientException ex) {
        log.warn("Upstream USAspending call failed", ex);
        return error(HttpStatus.BAD_GATEWAY, "Upstream USAspending API error", List.of());
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), message, details));
    }
}
