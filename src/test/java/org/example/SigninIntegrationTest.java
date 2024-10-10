package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import jakarta.transaction.Transactional;
import org.example.dto.UserSignInDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // Specify the test profile to use H2
public class SigninIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void testSignin(String email, String password, int expectedStatus) throws Exception {
        UserSignInDTO user = UserSignInDTO.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(expectedStatus));
    }

    // Load the database with predefined users for successful login test
    @Test
    @DatabaseSetup("/users.xml")
    public void testSuccessfulLogin() throws Exception {
        testSignin("z@z.com", "Zwa@2182003", 200);
    }

    // Load the database even for tests involving non-existing emails to ensure consistent setup
    @Test
    @DatabaseSetup("/users.xml")
    public void testLoginWithNonExistingEmail() throws Exception {
        testSignin("nonexistent@example.com", "Password123@", 401);
    }

    // Load the database for incorrect password scenario
    @Test
    @DatabaseSetup("/users.xml")
    public void testLoginWithIncorrectPassword() throws Exception {
        testSignin("testuser@example.com", "WrongPassword@", 401);
    }

    // No need for database setup for invalid email format test
    @Test
    public void testLoginWithInvalidEmailFormat() throws Exception {
        testSignin("invalidemail.com", "Password123@", 401);
    }
}
