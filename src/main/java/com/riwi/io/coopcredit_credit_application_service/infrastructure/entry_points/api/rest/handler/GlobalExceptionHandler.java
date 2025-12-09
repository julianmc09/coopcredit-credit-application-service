package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.handler;

import org.springframework.dao.DataIntegrityViolationException; // Import this
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setType(URI.create("https://example.com/problems/invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://example.com/problems/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // New handler for data integrity issues
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        problemDetail.setTitle("Data Conflict");
        problemDetail.setType(URI.create("https://example.com/problems/data-conflict"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://example.com/problems/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        // Optionally, log the full exception for debugging, but don't expose sensitive details
        // log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return problemDetail;
    }
}
