package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.util.ErrorMessagesUtil;
import com.buyit.ecommerce.util.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserTestUtils userTestUtils;
    private final ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil();

    private String keycloakUserId;

    @BeforeAll
    void setUpAll() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO = userTestUtils.getUserCredentials();
        authService.createUser(userRegisterDTO);
        keycloakUserId = extractKeycloakUserId(userRegisterDTO);
    }

    @AfterAll
    void tearDown() {
        userTestUtils.cleanUsers();
    }

    private String extractKeycloakUserId(UserRegisterDTO dto) throws JsonProcessingException {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserName(dto.getUserName());
        userLoginDTO.setPassword(dto.getPassword());

        AccessTokenResponse response = authService.login(userLoginDTO);
        return jwtDecoder.decode(response.getToken()).getSubject();
    }

    @Test
    void Should_ThrowConstraintViolationException_When_InvalidAddressParameters(){

        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("alias");
        createAddress.setCity(null);
        createAddress.setCountry(null);
        createAddress.setPostalCode("postalCode");
        createAddress.setStreet("street");


        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> addressService.createAddress(keycloakUserId, createAddress));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        assertThat(errorMessages.get("city")).contains("city cannot be blank");
        assertThat(errorMessages.get("country")).contains("country cannot be blank");
    }

    @Test
    void Should_CreateSuccessfulAddress_WhenValidAddressParameters(){
        CreateAddressRequest createAddress = createAddress();

        CreateAddressResponse response = addressService.createAddress(keycloakUserId, createAddress);

        assertNotNull(response);
        assertTrue(response.getCity().contains(response.getCity()));
        assertTrue(response.getCountry().contains(response.getCountry()));
        assertTrue(response.getPostalCode().contains(response.getPostalCode()));
        assertTrue(response.getStreet().contains(response.getStreet()));
    }

    @Test
    void Should_ThrowResourceNotFoundException_WhenAddressNotFound() {
        CreateAddressRequest createAddress = createAddress();

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        Long id = createdResponse.getId();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddress(keycloakUserId, id + 1));

        assertThat(exception.getMessage()).isEqualTo("User address not found");
    }

    @Test
    void Should_ThrowResourceNotFoundException_WhenUserNotFound(){
        CreateAddressRequest createAddress = createAddress();

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);
        Long id = createdResponse.getId();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddress(keycloakUserId + 1, id));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    void Should_UpdateAddress_When_ParametersAreValid() {
        CreateAddressRequest createAddress = createAddress();

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        UserAddressResponse address = addressService.getAddress(keycloakUserId, createdResponse.getId());

        assertThat(address).isNotNull();
        assertThat(address.getCity()).isEqualTo(createdResponse.getCity());
        assertThat(address.getCountry()).isEqualTo(createdResponse.getCountry());
        assertThat(address.getPostalCode()).isEqualTo(createdResponse.getPostalCode());
        assertThat(address.getStreet()).isEqualTo(createdResponse.getStreet());
    }

    @Test
    void Should_ThrowConstraintViolationException_When_InvalidUpdateAddressParameters(){
        CreateAddressRequest createAddress = createAddress();

        UpdateAddressRequest updateAddress = new UpdateAddressRequest();
        updateAddress.setCity("");
        updateAddress.setCountry("");
        updateAddress.setPostalCode("testpostalcode2");
        updateAddress.setStreet("teststreet2");

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        Long id = createdResponse.getId();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> addressService.updateAddress(keycloakUserId, id, updateAddress));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        assertThat(errorMessages.get("city")).contains("city cannot be blank");
        assertThat(errorMessages.get("country")).contains("country cannot be blank");
    }

    @Test
    void Should_UpdateAddressSuccessfully_When_ValidUpdateParameters(){
        CreateAddressRequest createAddress = createAddress();

        UpdateAddressRequest updateAddress = new UpdateAddressRequest();
        updateAddress.setAlias("AliasTest2");
        updateAddress.setCity("TESTCITY2");
        updateAddress.setCountry("TESTCOUNTRY2");
        updateAddress.setPostalCode("testpostalcode2");
        updateAddress.setStreet("teststreet2");

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        Long id = createdResponse.getId();
        UpdateAddressResponse response = addressService.updateAddress(keycloakUserId, id, updateAddress);

        assertThat(response).isNotNull();
        assertThat(response.getCity()).isEqualTo(updateAddress.getCity());
        assertThat(response.getCountry()).isEqualTo(updateAddress.getCountry());
        assertThat(response.getPostalCode()).isEqualTo(updateAddress.getPostalCode());
        assertThat(response.getStreet()).isEqualTo(updateAddress.getStreet());
    }


    private CreateAddressRequest createAddress(){

        String randomUUID = UUID.randomUUID().toString().substring(0, 8);

        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest"+randomUUID);
        createAddress.setCity("testcity"+randomUUID);
        createAddress.setCountry("testcountry"+randomUUID);
        createAddress.setPostalCode("testpostalcode"+randomUUID);
        createAddress.setStreet("teststreet"+randomUUID);

        return createAddress;
    }
}