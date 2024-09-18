package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.example.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        String validationError = UserValidator.validateUser(user);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userService.findByEmail(email);
        if (user == null) {
            // Return a JSON object with an error message
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        if (!userService.checkPassword(user, password)) {
            // Return a JSON object with an error message
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        // Return success response as JSON
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
