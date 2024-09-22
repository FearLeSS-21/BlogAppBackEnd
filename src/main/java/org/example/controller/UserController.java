package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }
}
