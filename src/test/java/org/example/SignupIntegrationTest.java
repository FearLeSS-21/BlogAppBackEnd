package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.dto.UserSignUpDTO;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // Specify the test profile to use H2
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
        UserSignUpDTO newUser = UserSignUpDTO.builder()
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
        User initialUser = User.builder()
                .email("testuser@example.com")
                .password(passwordEncoder.encode("Password123@"))
                .name("Original User")
                .build();
        userRepository.save(initialUser);

        testSignup("testuser@example.com", "NewPassword123@", "Another Test User", 409);

        User foundUser = userRepository.findByEmail("testuser@example.com").orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Original User");
        assertThat(passwordEncoder.matches("Password123@", foundUser.getPassword())).isTrue();
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
