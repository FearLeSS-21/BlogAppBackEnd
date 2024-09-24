package org.example.exception;

public class UserInvalidEmailFormatException extends RuntimeException {
    public UserInvalidEmailFormatException(String message) {
        super(message);
    }
}
