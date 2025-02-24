package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.config.TestContainersConfig;
import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
class AddressControllerTest extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;


    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private UsersRepository usersRepository;

    public String token;

    @BeforeEach()
    void setUp() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO;
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");
        userRegisterDTO.setRole("user");
        userRegisterDTO.setEmail("testuser@example.com");
        userRegisterDTO.setUserName("testuser");
        userRegisterDTO.setPassword("SecurePass123!");

        authService.createUser(userRegisterDTO);
        token = extractTokenFromUser(userRegisterDTO.getUserName(), userRegisterDTO.getPassword());

    }

    @AfterEach
    void tearDown() {

        List<User> users = usersRepository.findAll();
        for (User user : users) {
            keycloakService.deleteUserFromKeycloak(user.getKeycloakUserId());
        }
    }

    private String extractTokenFromUser(String username, String password) throws JsonProcessingException {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserName(username);
        userLoginDTO.setPassword(password);

        AccessTokenResponse response = authService.login(userLoginDTO);
        return response.getToken();
    }


    @Test
    @Transactional
    @Rollback
    void Should_Unauthorized_When_AuthorizationHeaderNotAttached() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        String jsonRequest = objectMapper.writeValueAsString(addressRequest);

        mockMvc.perform(post("/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors.message").value("Full authentication is required to access this resource"));

    }


    @Test
    @Transactional
    @Rollback
    void Should_CreatedFailed_When_InvalidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("");
        addressRequest.setCountry("");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        String jsonRequest = objectMapper.writeValueAsString(addressRequest);

        mockMvc.perform(post("/address")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.country").value("must not be blank"))
                .andExpect(jsonPath("$.errors.city").value("must not be blank"));
    }


    @Test
    @Transactional
    @Rollback
    void Should_CreateSuccessfully_When_ValidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        CreateAddressResponse addressResponse = createAddress(addressRequest);

        assertThat(addressResponse).isNotNull();
        assertThat(addressResponse.getCountry()).isEqualTo("CountryTest");
        assertThat(addressResponse.getCity()).isEqualTo("CityTest");
        assertThat(addressResponse.getStreet()).isEqualTo("StreetTest");
        assertThat(addressResponse.getPostalCode()).isEqualTo("PostalCodeTest");
    }

    @Test
    @Transactional
    @Rollback
    void Should_FailToUpdate_WhenInvalidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        // Crear una dirección válida
        CreateAddressResponse createdAddress = createAddress(addressRequest);

        // Intentar actualizar con parámetros inválidos (por ejemplo, postalCode vacío)
        UpdateAddressRequest invalidUpdateRequest = new UpdateAddressRequest();
        invalidUpdateRequest.setCity("NewCity");
        invalidUpdateRequest.setCountry("NewCountry");
        invalidUpdateRequest.setStreet("NewStreet");
        invalidUpdateRequest.setPostalCode(""); // Inválido

        String invalidUpdateJson = objectMapper.writeValueAsString(invalidUpdateRequest);

        mockMvc.perform(put("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUpdateJson))
                .andExpect(status().isBadRequest()) // Esperamos error 400
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.postal_code").value("must not be blank"));
    }


    @Test
    @Transactional
    @Rollback
    void Should_UpdateSuccessful_When_ValidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest();
        updateAddressRequest.setCity("CityTest2");
        updateAddressRequest.setCountry("CountryTest2");
        updateAddressRequest.setStreet("StreetTest2");
        updateAddressRequest.setPostalCode("PostalCodeTest2");

        String updateJsonRequest = objectMapper.writeValueAsString(updateAddressRequest);

        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(put("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address updated successfully"))
                .andExpect(jsonPath("$.data.country").value(updateAddressRequest.getCountry()))
                .andExpect(jsonPath("$.data.city").value(updateAddressRequest.getCity()))
                .andExpect(jsonPath("$.data.street").value(updateAddressRequest.getStreet()))
                .andExpect(jsonPath("$.data.postalCode").value(updateAddressRequest.getPostalCode()));
    }

    @Test
    @Transactional
    @Rollback
    void Should_DeleteFailed_When_AddressIdNotExists() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId() + 1)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found."));
    }

    @Test
    @Transactional
    @Rollback
    void Should_DeleteFailed_When_AddressExistsAndNotOwnThatAddress() throws Exception {

        UserRegisterDTO secondUser = new UserRegisterDTO();
        secondUser.setFirstName("Other");
        secondUser.setLastName("User");
        secondUser.setEmail("otheruser@example.com");
        secondUser.setUserName("otheruser");
        secondUser.setPassword("SecurePass123!");

        authService.createUser(secondUser);
        String secondUserToken = extractTokenFromUser(secondUser.getUserName(), secondUser.getPassword());

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + secondUserToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"))
                .andExpect(jsonPath("$.errors.message").value("You cannot delete this resource."));
    }

    @Test
    @Transactional
    @Rollback
    void Should_DeleteSuccessful_When_IdExistsAndOwnership() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Address deleted successfully"))
                .andReturn();
    }

    private CreateAddressResponse createAddress(CreateAddressRequest requestAddress) throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity(requestAddress.getCity());
        request.setCountry(requestAddress.getCountry());
        request.setStreet(requestAddress.getStreet());
        request.setPostalCode(requestAddress.getPostalCode());

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/address")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated()) // Verifica que se creó correctamente
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return objectMapper.treeToValue(root.get("data"), CreateAddressResponse.class);
    }
}
