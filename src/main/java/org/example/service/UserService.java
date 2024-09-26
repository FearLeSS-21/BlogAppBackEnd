package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.example.dto.UserSignInDTO;
import org.example.dto.UserSignUpDTO;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.example.viewmodel.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public UserViewModel registerUser(UserSignUpDTO userSignUpDTO) {

        userRepository.findByEmail(userSignUpDTO.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException("Email already in use");
        });


        User user = objectMapper.convertValue(userSignUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));


        User savedUser = userRepository.save(user);
        return mapToUserViewModel(savedUser);
    }

    public String loginUser(@Valid UserSignInDTO userDTO) {
        // Validate user
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check password
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid password");
        }

        // Generate and return JWT token
        return jwtUtil.generateToken(user.getEmail());
    }

    private UserViewModel mapToUserViewModel(User user) {
        return objectMapper.convertValue(user, UserViewModel.class);
    }
}
