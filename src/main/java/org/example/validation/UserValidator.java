package org.example.validation;

import org.example.model.User;

public class UserValidator {

    public static String validateUser(User user) {
        if (user == null) {
            return "User cannot be null";
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return "Email is required";
        }

        if (!user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Invalid email format";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Password is required";
        }

        if (user.getPassword().length() < 8) {
            return "Password must be at least 8 characters long";
        }

        if (!user.getPassword().matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }

        if (!user.getPassword().matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }

        if (!user.getPassword().matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        if (!user.getPassword().matches(".*[!@#$%^&*()].*")) {
            return "Password must contain at least one special character";
        }

        return null; // Return null if there are no validation errors
    }
}
