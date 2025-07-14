package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.util.UserTestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private KeycloakService keycloakService;

    private static UserRegisterDTO userRegisterDTO;

    @Autowired
    private UserTestUtils userTestUtils;

    @BeforeAll
    void setUp() {
        userTestUtils.cleanUsers();
    }

    @BeforeEach
    void setUpEach() {
        userRegisterDTO = userTestUtils.getUserCredentials();
    }

    @AfterAll
    void tearDown() {
        userTestUtils.cleanUsers();
    }

    @Test
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
                .andExpect(jsonPath("$.errors.firstname").exists())
                .andExpect(jsonPath("$.errors.lastname").exists());
    }

    @Test
    void Should_RegisterSuccessful_WhenValidCredentials() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRegisterDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void Should_LoginFailed_When_CredentialsNotMeetRequirements() throws Exception {
        authService.createUser(userRegisterDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO("test", "Secure");
        String jsonRequest = objectMapper.writeValueAsString(userLoginDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    void Should_LoginSuccessful_When_CredentialsAreValid() throws Exception {
        authService.createUser(userRegisterDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO(userRegisterDTO.getUserName(), userRegisterDTO.getPassword());
        String jsonRequest = objectMapper.writeValueAsString(userLoginDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.access_token").exists());
    }

    @Test
    void Should_ProviderNotFound_When_ProviderIsNotEnabled() throws Exception {
        String provider = "facebook";

        mockMvc.perform(get("/auth/provider/{provider}", provider))
                .andExpect(status().isNotFound());
    }

    @Test
    void Should_RedirectToProvider_When_ProviderExistAndIsEnabled() throws Exception {
        String provider = "google";
        String redirectUrl = "http://localhost:80/api/auth/callback";
        String expectedAuthUrl = keycloakService.getRedirectProvider(provider, redirectUrl);

        MvcResult result = mockMvc.perform(get("/auth/provider/{provider}", provider))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode dataNode = rootNode.path("data");

        assertNotNull(dataNode.asText());
        assertEquals(dataNode.asText(), expectedAuthUrl);
    }
}
