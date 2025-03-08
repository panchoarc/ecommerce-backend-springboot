package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.config.TestContainersConfig;
import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class AuthControllerTest extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private KeycloakService keycloakService;

    private UserRegisterDTO userRegisterDTO;

    @BeforeEach
    void setUp() {
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");
        userRegisterDTO.setEmail("testuser@example.com");
        userRegisterDTO.setUserName("testuser");
        userRegisterDTO.setPassword("SecurePass123!");
    }

    @AfterEach
    void tearDown() {
        Optional<User> user = usersRepository.findByEmail(userRegisterDTO.getEmail());
        user.ifPresent(value -> keycloakService.deleteUserFromKeycloak(value.getKeycloakUserId()));
    }

    @Test
    @Transactional
    @Rollback
    void Should_RegisterFail_WhenInvalidParametersRequest() throws Exception {

        UserRegisterDTO userRegister = new UserRegisterDTO();
        userRegister.setEmail("testuser@example.com");
        userRegister.setUserName("testuser");
        userRegister.setPassword("SecurePass123!");

        String jsonRequest = objectMapper.writeValueAsString(userRegister);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstname").value("firstname cannot be blank"))
                .andExpect(jsonPath("$.errors.lastname").value("lastname cannot be blank"));
    }

    @Test
    @Transactional
    @Rollback
    void Should_RegisterSuccessful_WhenValidCredentials() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRegisterDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @Transactional
    @Rollback
    void Should_LoginFailed_When_CredentialsNotMeetRequirements() throws Exception {
        authService.createUser(userRegisterDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO("test", "Secure");
        String jsonRequest = objectMapper.writeValueAsString(userLoginDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").value("Your password must have between 8 and 20 characters"))
                .andExpect(jsonPath("$.errors.username").value("username must have between 6 and 50 characters."));
    }

    @Test
    @Transactional
    @Rollback
    void Should_LoginSuccessful_When_CredentialsAreValid() throws Exception {
        authService.createUser(userRegisterDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO("testuser", "SecurePass123!");
        String jsonRequest = objectMapper.writeValueAsString(userLoginDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.access_token").exists());
    }

    @Test
    @Transactional
    @Rollback
    void Should_ProviderNotFound_When_ProviderIsNotEnabled() throws Exception {
        String provider = "facebook";

        mockMvc.perform(get("/auth/provider/{provider}", provider))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Rollback
    void Should_RedirectToProvider_When_ProviderExistAndIsEnabled() throws Exception {
        String provider = "google";
        String redirectUrl = "http://localhost:80/api/auth/callback";
        String expectedAuthUrl = keycloakService.getRedirectProvider(provider, redirectUrl);

        MvcResult result = mockMvc.perform(get("/auth/provider/{provider}", provider))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String actualRedirectUrl = result.getResponse().getHeader("Location");

        assertNotNull(actualRedirectUrl, "El header 'Location' no debe ser nulo");
        assertEquals(actualRedirectUrl, expectedAuthUrl);
    }
}
