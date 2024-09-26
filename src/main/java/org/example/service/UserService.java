package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.example.dto.UserSignInDTO;
import org.example.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.example.model.User;
import org.example.dto.UserSignUpDTO;
import org.example.viewmodel.UserViewModel;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public UserViewModel registerUser(UserSignUpDTO userSignUpDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userSignUpDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = objectMapper.convertValue(userSignUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToUserViewModel(savedUser);
    }

    public UserViewModel loginUser(@Valid UserSignInDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid password");
        }

        return mapToUserViewModel(user);
    }

    private UserViewModel mapToUserViewModel(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            return objectMapper.convertValue(user, UserViewModel.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert User to UserViewModel", e);
        }
    }

}
