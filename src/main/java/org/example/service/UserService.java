package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registers a new user with an encrypted password
    public User registerUser(User user) {
        // Encrypt the plain text password before saving to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Verifies if the raw password matches the encrypted one stored in the database
    public boolean checkPassword(User user, String rawPassword) {
        // Compare raw password with the stored encrypted password
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}


