package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.AuthenticationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.util.ErrorMessagesUtil;
import com.buyit.ecommerce.util.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserTestUtils userTestUtils;

    private UserRegisterDTO registerDTO;
    private final ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil();

    // Mensajes de error esperados
    private static final String EMAIL_INVALID_FORMAT = "email hasn't a valid format";
    private static final String EMAIL_BLANK = "email cannot be blank";
    private static final String FIRSTNAME_BLANK = "firstname cannot be blank";
    private static final String PASSWORD_LENGTH = "Your password must have between 8 and 20 characters";
    private static final String USERNAME_INVALID_LENGTH = "username must have between 6 and 50 characters.";


    @BeforeAll
    void setUpAll() {
        registerDTO = userTestUtils.getUserCredentials();
        authService.createUser(registerDTO);
    }


    @AfterAll
    void tearDown() {
        userTestUtils.cleanUsers();
    }

    @Test
    void Should_ThrowConstraintViolationException_When_InvalidUserFields() {

        UserRegisterDTO invalidDTO = new UserRegisterDTO();
        invalidDTO.setFirstName(null);  // Nombre vacío
        invalidDTO.setLastName("User");
        invalidDTO.setEmail(null);      // Email vacío
        invalidDTO.setUserName("testuser");
        invalidDTO.setPassword("123"); // Contraseña insegura

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> authService.createUser(invalidDTO));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        assertTrue(errorMessages.get("email").contains(EMAIL_BLANK));
        assertTrue(errorMessages.get("email").contains(EMAIL_INVALID_FORMAT));
        assertTrue(errorMessages.get("firstName").contains(FIRSTNAME_BLANK));
        assertTrue(errorMessages.get("password").contains(PASSWORD_LENGTH));
    }

    @Test
    void Should_ThrowResourceExistException_When_UserAlreadyExists() {

        ResourceExistException thrown = assertThrows(ResourceExistException.class, () -> authService.createUser(registerDTO));
        assertThat(thrown.getMessage()).contains("Email or username is already in use");
    }

    @Test
    void Should_CreateUserSuccessfully_WhenValidFields() {

        User user = usersRepository.findByEmail(registerDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        assertThat(user.getFirstName()).isEqualTo(registerDTO.getFirstName());
        assertThat(user.getLastName()).isEqualTo(registerDTO.getLastName());
        assertThat(user.getEmail()).isEqualTo(registerDTO.getEmail());
    }

    @Test
    void Should_ThrowConstraintViolationException_When_InvalidLoginFields() {
        UserLoginDTO invalidDTO = new UserLoginDTO();
        invalidDTO.setUserName("test");
        invalidDTO.setPassword("Secure");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> authService.login(invalidDTO));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        log.info("ERRORS: {}", errorMessages);

        assertTrue(errorMessages.get("userName").contains(USERNAME_INVALID_LENGTH));
        assertTrue(errorMessages.get("password").contains(PASSWORD_LENGTH));
    }

    @Test
    void Should_ThrowAuthenticationException_When_InvalidCredentials() {

        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName(registerDTO.getUserName());
        validUser.setPassword("WrongPassword123!");

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authService.login(validUser));

        assertThat(exception.getMessage()).contains("Invalid credentials");
    }

    @Test
    void Should_ThrowResourceNotFoundException_When_UserNotFoundDuringLogin() {
        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName("nonexistentUser");
        validUser.setPassword("SecurePass123!");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> authService.login(validUser));

        assertThat(exception.getMessage()).contains("User not found");
    }

    @Test
    void Should_LoginSuccessfully_When_ValidCredentials() throws JsonProcessingException {
        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName(registerDTO.getUserName());
        validUser.setPassword(registerDTO.getPassword());

        AccessTokenResponse logged = authService.login(validUser);

        // Verificar que el token de acceso no sea nulo ni vacío
        assertNotNull(logged);
        assertNotNull(logged.getToken());
    }
}
