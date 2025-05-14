package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.util.ErrorMessagesUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private KeycloakService keycloakService;

    private final ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil();

    private UserRegisterDTO userRegisterDTO;

    @BeforeEach()
    void setUp() {
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");
        userRegisterDTO.setRole("user");
        userRegisterDTO.setEmail("testuser@example.com");
        userRegisterDTO.setUserName("testuser");
        userRegisterDTO.setPassword("SecurePass123!");
    }

    @AfterEach
    void tearDown() {
        Optional<User> user = usersRepository.findByEmail(userRegisterDTO.getEmail());
        user.ifPresent(value -> keycloakService.deleteUserFromKeycloak(value.getKeycloakUserId()));
    }

    private String extractKeycloakUserId() throws JsonProcessingException {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserName("testuser");
        userLoginDTO.setPassword("SecurePass123!");

        AccessTokenResponse response = authService.login(userLoginDTO);
        return jwtDecoder.decode(response.getToken()).getSubject();
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowConstraintViolationException_When_InvalidAddressParameters() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("");
        createAddress.setCountry("");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> addressService.createAddress(keycloakUserId, createAddress));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        assertThat(errorMessages.get("city")).contains("city cannot be blank");
        assertThat(errorMessages.get("country")).contains("country cannot be blank");
    }

    @Test
    @Transactional
    @Rollback
    void Should_CreateSuccessfulAddress_WhenValidAddressParameters() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

        CreateAddressResponse response = addressService.createAddress(keycloakUserId, createAddress);

        assertNotNull(response);
        assertThat(response.getCity()).isEqualTo("testcity");
        assertThat(response.getCountry()).isEqualTo("testcountry");
        assertThat(response.getPostalCode()).isEqualTo("testpostalcode");
        assertThat(response.getStreet()).isEqualTo("teststreet");
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowResourceNotFoundException_WhenAddressNotFound() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        Long id = createdResponse.getId();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddress(keycloakUserId, id + 1));

        assertThat(exception.getMessage()).isEqualTo("User address not found");
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowResourceNotFoundException_WhenUserNotFound() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);
        Long id = createdResponse.getId();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddress(keycloakUserId + 1, id));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @Transactional
    @Rollback
    void Should_UpdateAddress_When_ParametersAreValid() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

        CreateAddressResponse createdResponse = addressService.createAddress(keycloakUserId, createAddress);

        UserAddressResponse address = addressService.getAddress(keycloakUserId, createdResponse.getId());

        assertThat(address).isNotNull();
        assertThat(address.getCity()).isEqualTo(createdResponse.getCity());
        assertThat(address.getCountry()).isEqualTo(createdResponse.getCountry());
        assertThat(address.getPostalCode()).isEqualTo(createdResponse.getPostalCode());
        assertThat(address.getStreet()).isEqualTo(createdResponse.getStreet());
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowConstraintViolationException_When_InvalidUpdateAddressParameters() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

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
    @Transactional
    @Rollback
    void Should_UpdateAddressSuccessfully_When_ValidUpdateParameters() throws JsonProcessingException {
        authService.createUser(userRegisterDTO);

        String keycloakUserId = extractKeycloakUserId();
        CreateAddressRequest createAddress = new CreateAddressRequest();
        createAddress.setAlias("AliasTest");
        createAddress.setCity("testcity");
        createAddress.setCountry("testcountry");
        createAddress.setPostalCode("testpostalcode");
        createAddress.setStreet("teststreet");

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
}