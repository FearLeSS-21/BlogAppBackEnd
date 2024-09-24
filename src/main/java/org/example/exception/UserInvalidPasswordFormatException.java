package org.example.exception;
public class UserInvalidPasswordFormatException extends RuntimeException {
    public UserInvalidPasswordFormatException(String message) {
        super(message);
    }
}