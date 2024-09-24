package org.example.exception;

import org.example.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(UserInvalidPasswordFormatException.class)
    public ResponseEntity<ErrorDTO> handleInvalidPasswordFormat(UserInvalidPasswordFormatException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .message(ex.getMessage())
                .details("Password format is invalid. Please ensure it meets the required criteria.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserInvalidEmailFormatException.class)
    public ResponseEntity<ErrorDTO> handleInvalidEmailFormat(UserInvalidEmailFormatException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .message(ex.getMessage())
                .details("Email format is invalid. Please provide a valid email address.")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }




}
