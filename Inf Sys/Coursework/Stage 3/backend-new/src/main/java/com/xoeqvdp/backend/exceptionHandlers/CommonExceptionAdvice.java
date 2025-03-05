package com.xoeqvdp.backend.exceptionHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class CommonExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(CommonExceptionAdvice.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex){
        log.warn("AuthenticationException occurred during execution: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse("Authorization error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public  ResponseEntity<ErrorResponse> handleRuntimeException(Exception ex) {
        log.warn("RuntimeException occurred during execution: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse("Runtime error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public  ResponseEntity<ErrorResponse> handleIOException(Exception ex) {
        log.warn("IOException occurred during execution: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse("IO error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ErrorResponse(String error, String message) {}
}
