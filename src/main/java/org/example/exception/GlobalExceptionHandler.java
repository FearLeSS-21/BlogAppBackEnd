package org.example.exception;

import org.example.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .message(ex.getMessage())
                .details("Please use another email address.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Handle other exceptions here
}
