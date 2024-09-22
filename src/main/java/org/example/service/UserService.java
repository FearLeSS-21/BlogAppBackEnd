package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.example.model.User;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> handleUserSignup(User user) {
        Optional<User> existingUser = findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        return ResponseEntity.ok(registerUser(user));
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
