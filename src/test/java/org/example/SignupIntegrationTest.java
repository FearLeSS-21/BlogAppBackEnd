package org.example;

import jakarta.transaction.Transactional;
import org.example.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private void testSignup(String email, String password, String name, int expectedStatus) throws Exception {
        UserDTO newUser = UserDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    public void testSuccessfulSignup() throws Exception {
        testSignup("testuser@example.com", "Password123@", "Test User", 201);

        User savedUser = userRepository.findByEmail("testuser@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(passwordEncoder.matches("Password123@", savedUser.getPassword())).isTrue();
    }

    @Test
    public void testSignupWithExistingEmail() throws Exception {
        // First, create a user to establish the existing email
        User initialUser = User.builder()
                .email("testuser@example.com")
                .password(passwordEncoder.encode("Password123@"))
                .name("Original User")
                .build();
        userRepository.save(initialUser);

        // Attempt to sign up again with the same email but different name and password
        testSignup("testuser@example.com", "NewPassword123@", "Another Test User", 409); // Expect conflict status

        // Verify that the initial user still exists and is unchanged
        User foundUser = userRepository.findByEmail("testuser@example.com").orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Original User"); // Ensure name remains unchanged
        assertThat(passwordEncoder.matches("Password123@", foundUser.getPassword())).isTrue(); // Ensure password remains unchanged
    }

    @Test
    public void testSignupWithInvalidPassword() throws Exception {
        testSignup("testuserz@example.com", "Password", "Test User", 400);
    }

    @Test
    public void testSignupWithInvalidEmailFormat() throws Exception {
        testSignup("invalidemail.com", "Password123!", "Test User", 400);
    }
}
