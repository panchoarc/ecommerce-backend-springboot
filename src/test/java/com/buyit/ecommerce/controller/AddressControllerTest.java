package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.util.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String userToken;

    @Autowired
    private UserTestUtils userTestUtils;

    @BeforeAll
    void setUp() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO = userTestUtils.getUserCredentials();
        UserRegisterDTO adminRegisterDTO = userTestUtils.getAdminCredentials();

        adminToken = userTestUtils.getToken(adminRegisterDTO);
        userToken = userTestUtils.getToken(userRegisterDTO);
    }

    @AfterAll
    void tearDown() {
        userTestUtils.cleanUsers();
    }

    @Test
    void Should_ThrowUnAuthorized_When_AuthNotProvided() throws Exception {

        mockMvc.perform(get("/address/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void Should_ReturnMyAddress_When_AuthorizationIsProvided() throws Exception {

        mockMvc.perform(get("/address/search")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void Should_ThrowUnauthorized_When_AuthorizationIsProvided() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        CreateAddressResponse addressResponse = createAddress(addressRequest);

        mockMvc.perform(get("/address/{id}", addressResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void Should_AddressNotFound_When_AddressNotExistAndAuthorizationIsProvided() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        CreateAddressResponse addressResponse = createAddress(addressRequest);

        mockMvc.perform(get("/address/{id}", addressResponse.getId() + 1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").exists());
    }


    @Test
    void Should_ReturnAddress_When_AddressExistsAndAuthorizationIsProvided() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        CreateAddressResponse addressResponse = createAddress(addressRequest);

        mockMvc.perform(get("/address/{id}", addressResponse.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.city").value(addressResponse.getCity()))
                .andExpect(jsonPath("$.data.country").value(addressResponse.getCountry()))
                .andExpect(jsonPath("$.data.street").value(addressResponse.getStreet()))
                .andExpect(jsonPath("$.data.postal_code").value(addressResponse.getPostalCode()));
    }

    @Test
    void Should_Unauthorized_When_AuthorizationHeaderNotAttached() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        String jsonRequest = objectMapper.writeValueAsString(addressRequest);

        mockMvc.perform(post("/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors.message").exists());

    }


    @Test
    void Should_CreatedFailed_When_InvalidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("");
        addressRequest.setCountry("");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        String jsonRequest = objectMapper.writeValueAsString(addressRequest);

        mockMvc.perform(post("/address")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.country").exists())
                .andExpect(jsonPath("$.errors.city").exists());
    }


    @Test
    void Should_CreateSuccessfully_When_ValidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
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
    void Should_FailToUpdate_WhenInvalidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        CreateAddressResponse createdAddress = createAddress(addressRequest);

        UpdateAddressRequest invalidUpdateRequest = new UpdateAddressRequest();
        invalidUpdateRequest.setCity("NewCity");
        invalidUpdateRequest.setCountry("NewCountry");
        invalidUpdateRequest.setStreet("NewStreet");
        invalidUpdateRequest.setPostalCode("");

        String invalidUpdateJson = objectMapper.writeValueAsString(invalidUpdateRequest);

        mockMvc.perform(put("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUpdateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.postal_code").exists());
    }


    @Test
    void Should_UpdateSuccessful_When_ValidParameters() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");

        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest();
        updateAddressRequest.setAlias("AliasTest2");
        updateAddressRequest.setCity("CityTest2");
        updateAddressRequest.setCountry("CountryTest2");
        updateAddressRequest.setStreet("StreetTest2");
        updateAddressRequest.setPostalCode("PostalCodeTest2");

        String updateJsonRequest = objectMapper.writeValueAsString(updateAddressRequest);

        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(put("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + adminToken)
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
    void Should_DeleteFailed_When_AddressIdNotExists() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId() + 1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found."));
    }

    @Test
    void Should_DeleteFailed_When_AddressExistsAndNotOwnThatAddress() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"))
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    void Should_DeleteSuccessful_When_IdExistsAndOwnership() throws Exception {

        CreateAddressRequest addressRequest = new CreateAddressRequest();
        addressRequest.setAlias("AliasTest");
        addressRequest.setCity("CityTest");
        addressRequest.setCountry("CountryTest");
        addressRequest.setStreet("StreetTest");
        addressRequest.setPostalCode("PostalCodeTest");


        CreateAddressResponse createdAddress = createAddress(addressRequest);

        mockMvc.perform(delete("/address/{id}", createdAddress.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Address deleted successfully"))
                .andReturn();
    }

    private CreateAddressResponse createAddress(CreateAddressRequest requestAddress) throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setAlias(requestAddress.getAlias());
        request.setCity(requestAddress.getCity());
        request.setCountry(requestAddress.getCountry());
        request.setStreet(requestAddress.getStreet());
        request.setPostalCode(requestAddress.getPostalCode());

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/address")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return objectMapper.treeToValue(root.get("data"), CreateAddressResponse.class);
    }
}
