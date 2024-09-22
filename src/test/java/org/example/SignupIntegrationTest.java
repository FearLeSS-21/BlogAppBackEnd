package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.builder.UserBuilder;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    @Test
    public void testSuccessfulSignup() throws Exception {
        // Create a new user using UserBuilder
        User newUser = new UserBuilder()
                .withEmail("testuser@example.com")
                .withPassword("Password123!")
                .withName("Test User")
                .build();

        // Convert User object to JSON
        String newUserJson = objectMapper.writeValueAsString(newUser);

        // Perform POST request to signup endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("testuser@example.com")))
                .andExpect(content().string(containsString("Test User")));

        // Assert that the user was saved to the database
        User savedUser = userRepository.findByEmail("testuser@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
    }

    @Test
    public void testSignupWithExistingEmail() throws Exception {
        // Pre-create a user in the database
        User existingUser = new UserBuilder()
                .withEmail("testuser@example.com")
                .withPassword(passwordEncoder.encode("Password123!"))
                .withName("Test User")
                .build();
        userRepository.save(existingUser);

        // Try to sign up with the same email
        User newUser = new UserBuilder()
                .withEmail("testuser@example.com")
                .withPassword("Password123!")
                .withName("Test User")
                .build();

        String newUserJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Email already in use")));
    }

    @Test
    public void testSignupWithInvalidPassword() throws Exception {
        User newUser = new UserBuilder()
                .withEmail("testuser@example.com")
                .withPassword("pass") // Invalid password
                .withName("Test User")
                .build();

        String newUserJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters long")));
    }

    @Test
    public void testSignupWithInvalidEmailFormat() throws Exception {
        User newUser = new UserBuilder()
                .withEmail("invalid-email") // Invalid email format
                .withPassword("Password123!")
                .withName("Test User")
                .build();

        String newUserJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));
    }
}
