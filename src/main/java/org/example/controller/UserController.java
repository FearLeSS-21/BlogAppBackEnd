package org.example.controller;

import org.example.dto.UserSignUpDTO;
import org.example.dto.UserSignInDTO;
import org.example.service.UserService;
import org.example.viewmodel.UserViewModel;
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
    public ResponseEntity<UserViewModel> signup(@Valid @RequestBody UserSignUpDTO userSignUpDTO) {
        UserViewModel userViewModel = userService.registerUser(userSignUpDTO);
        return ResponseEntity.status(201).body(userViewModel);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserViewModel> login(@Valid @RequestBody UserSignInDTO userDTO) {
        UserViewModel userViewModel = userService.loginUser(userDTO);
        return ResponseEntity.ok(userViewModel);
    }
}
