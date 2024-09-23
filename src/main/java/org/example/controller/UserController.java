package org.example.controller;

import org.example.dto.UserDTO;
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
    public ResponseEntity<UserViewModel> signup(@Valid @RequestBody UserDTO userDTO) {
        UserViewModel userViewModel = userService.registerUser(userDTO);
        return ResponseEntity.status(201).body(userViewModel);
    }
}
