package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.dto.UserSignInDTO;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SigninIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private void testSignin(String email, String password, int expectedStatus) throws Exception {
        UserSignInDTO user = UserSignInDTO.builder().email(email).password(password).build();

        mockMvc.perform(post("/users/signin").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().is(expectedStatus));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        User user = User.builder().email("z@z.com").password(passwordEncoder.encode("Zwa@2182003")).build();
        userRepository.save(user);

        testSignin("z@z.com", "Zwa@2182003", 200);
    }

    @Test
    public void testLoginWithNonExistingEmail() throws Exception {
        testSignin("nonexistent@example.com", "Password123@", 401);
    }

    @Test
    public void testLoginWithIncorrectPassword() throws Exception {
        // Setup: create a user
        User user = User.builder().email("testuser@example.com").password(passwordEncoder.encode("Password123@")).build();
        userRepository.save(user);

        testSignin("testuser@example.com", "WrongPassword@", 400);
    }

    @Test
    public void testLoginWithInvalidEmailFormat() throws Exception {
        testSignin("invalidemail.com", "Password123@", 400);
    }
}
