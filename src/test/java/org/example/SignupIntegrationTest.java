package org.example;

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


    @Test
    public void testSuccessfulSignup() throws Exception {
        String newUserJson = "{\"email\": \"testuser@example.com\", \"password\": \"Password123!\", \"name\": \"Test User\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("testuser@example.com")))
                .andExpect(content().string(containsString("Test User")));
    }

    @Test
    public void testSignupWithExistingEmail() throws Exception {
        // Pre-create a user in the database
        User existingUser = new User();
        existingUser.setEmail("testuser@example.com");
        existingUser.setPassword(passwordEncoder.encode("Password123!"));
        existingUser.setName("Test User");
        userRepository.save(existingUser);

        String newUserJson = "{\"email\": \"testuser@example.com\", \"password\": \"Password123!\", \"name\": \"Test User\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Email already in use")));
    }

    @Test
    public void testSignupWithInvalidPassword() throws Exception {
        String newUserJson = "{\"email\": \"testuser@example.com\", \"password\": \"pass\", \"name\": \"Test User\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters long")));
    }

    @Test
    public void testSignupWithInvalidEmailFormat() throws Exception {
        String newUserJson = "{\"email\": \"invalid-email\", \"password\": \"Password123!\", \"name\": \"Test User\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));
    }
}
