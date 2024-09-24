package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.example.model.User;
import org.example.dto.UserDTO;
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

    public UserViewModel registerUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = mapToUser(userDTO); // Changed
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToUserViewModel(savedUser); // Changed
    }

    private User mapToUser(UserDTO userDTO) { // New method
        try {
            return objectMapper.convertValue(userDTO, User.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert UserDTO to User", e);
        }
    }

    private UserViewModel mapToUserViewModel(User user) { // New method
        return UserViewModel.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }


}
