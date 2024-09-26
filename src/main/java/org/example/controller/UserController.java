package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.LoginResponseDTO;
import org.example.dto.UserSignInDTO;
import org.example.dto.UserSignUpDTO;
import org.example.service.UserService;
import org.example.viewmodel.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserViewModel> signup(@Valid @RequestBody UserSignUpDTO userSignUpDTO) {
        UserViewModel userViewModel = userService.registerUser(userSignUpDTO);
        return ResponseEntity.status(201).body(userViewModel);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody UserSignInDTO userDTO) {
        String jwtToken = userService.loginUser(userDTO);  // Assuming loginUser returns a string token
        LoginResponseDTO response = new LoginResponseDTO(jwtToken, "Login successful");
        return ResponseEntity.ok(response);
    }

}
