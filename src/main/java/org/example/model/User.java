package org.example.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^(?=.*example)([^@\\s]+)@([^@\\s]+\\.[^@\\s]+)$",
            message = "Email must contain 'example' and have a valid format with exactly one '@' symbol.")
    private String email;


    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    private String password;

    @NotBlank(message = "Name is mandatory")
    private String name;
}