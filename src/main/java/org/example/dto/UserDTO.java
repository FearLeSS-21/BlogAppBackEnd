package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^(?=.*example)([^@\\s]+)@([^@\\s]+\\.[^@\\s]+)$",
            message = "Email must contain 'example' and have a valid format with exactly one '@' symbol.")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    @NotBlank(message = "Name is mandatory")
    private String name;
}
