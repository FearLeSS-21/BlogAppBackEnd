package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserSignInDTO;
import org.example.dto.UserSignUpDTO;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.example.viewmodel.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserViewModel registerUser(UserSignUpDTO userSignUpDTO) {
        userRepository.findByEmail(userSignUpDTO.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException("Email already in use");
        });

        User user = objectMapper.convertValue(userSignUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));
        User savedUser = userRepository.save(user);

        return mapToUserViewModel(savedUser);
    }

    public String loginUser(UserSignInDTO userDTO) {
        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Return JWT token after successful authentication
            return jwtUtil.generateToken(userDTO.getEmail());

        } catch (Exception e) {

            throw new UserNotFoundException("Invalid email or password");
        }
    }

    private UserViewModel mapToUserViewModel(User user) {
        try {
            return objectMapper.convertValue(user, UserViewModel.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert User to UserViewModel", e);
        }
    }
}
