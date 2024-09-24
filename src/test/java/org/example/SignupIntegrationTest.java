package org.example;

import org.example.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class SignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }


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
        userRepository.save(new User("testuser@example.com", passwordEncoder.encode("Password123!"), "Test User"));
        testSignup("testuser@example.com", "Password123!", "Test User", 400);
    }

    @Test
    public void testSignupWithInvalidPassword() throws Exception {
        testSignup("testuserz@example.com", "Password", "Test User", 400);
    }

    @Test
    public void testSignupWithInvalidEmailFormat() throws Exception {
        testSignup("invalid@email.com", "Password123!", "Test User", 400);
    }
}
