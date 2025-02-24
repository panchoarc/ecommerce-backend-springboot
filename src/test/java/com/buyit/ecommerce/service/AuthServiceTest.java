package com.buyit.ecommerce.service;

import com.buyit.ecommerce.config.TestContainersConfig;
import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.AuthenticationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.util.ErrorMessagesUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthServiceTest extends TestContainersConfig {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private KeycloakService keycloakService;

    private UserRegisterDTO registerDTO;
    private final ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil();

    // Mensajes de error esperados
    private static final String EMAIL_INVALID_FORMAT = "Email hasn't a valid format";
    private static final String EMAIL_BLANK = "Email cannot be blank.";
    private static final String FIRSTNAME_BLANK = "First name cannot be blank";
    private static final String PASSWORD_LENGTH = "Your password must have between 8 and 20 characters";
    private static final String USERNAME_INVALID_LENGTH = "Username must have between 6 and 50 characters.";

    @BeforeEach
    void setUp() {
        // Setup de usuario de prueba
        registerDTO = new UserRegisterDTO();
        registerDTO.setFirstName("Test");
        registerDTO.setLastName("User");
        registerDTO.setRole("user");
        registerDTO.setEmail("testuser@example.com");
        registerDTO.setUserName("testuser");
        registerDTO.setPassword("SecurePass123!");
    }

    @AfterEach
    void tearDown() {
        // Limpieza de usuario creado
        Optional<User> user = usersRepository.findByEmail(registerDTO.getEmail());
        user.ifPresent(value -> keycloakService.deleteUserFromKeycloak(value.getKeycloakUserId()));
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowConstraintViolationException_When_InvalidUserFields() {
        authService.createUser(registerDTO);

        UserRegisterDTO invalidDTO = new UserRegisterDTO();
        invalidDTO.setFirstName(null);  // Nombre vacío
        invalidDTO.setLastName("User");
        invalidDTO.setEmail(null);      // Email vacío
        invalidDTO.setUserName("testuser");
        invalidDTO.setPassword("123"); // Contraseña insegura

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> authService.createUser(invalidDTO));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        // Verificaciones de mensajes de error
        assertTrue(errorMessages.containsKey("email"));
        assertTrue(errorMessages.containsKey("password"));
        assertTrue(errorMessages.get("email").contains(EMAIL_BLANK));
        assertTrue(errorMessages.get("email").contains(EMAIL_INVALID_FORMAT));
        assertTrue(errorMessages.get("firstName").contains(FIRSTNAME_BLANK));
        assertTrue(errorMessages.get("password").contains(PASSWORD_LENGTH));
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowResourceExistException_When_UserAlreadyExists() {
        // Crear usuario primero
        authService.createUser(registerDTO);

        // Intentar crear el mismo usuario
        ResourceExistException thrown = assertThrows(ResourceExistException.class, () -> authService.createUser(registerDTO));
        assertThat(thrown.getMessage()).contains("Email or username is already in use");
    }

    @Test
    @Transactional
    @Rollback
    void Should_CreateUserSuccessfully_WhenValidFields() {
        // Crear usuario correctamente
        authService.createUser(registerDTO);

        User user = usersRepository.findByEmail(registerDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getEmail()).isEqualTo(registerDTO.getEmail());
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowConstraintViolationException_When_InvalidLoginFields() {

        authService.createUser(registerDTO);

        UserLoginDTO invalidDTO = new UserLoginDTO();
        invalidDTO.setUserName("test");
        invalidDTO.setPassword("Secure");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> authService.login(invalidDTO));

        Map<String, List<String>> errorMessages = errorMessagesUtil.getErrorMessages(exception);

        log.info("Error Messages {}", errorMessages);

        // Verificaciones de mensajes de error
        assertTrue(errorMessages.containsKey("userName"));
        assertTrue(errorMessages.containsKey("password"));
        assertTrue(errorMessages.get("userName").contains(USERNAME_INVALID_LENGTH));
        assertTrue(errorMessages.get("password").contains(PASSWORD_LENGTH));
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowAuthenticationException_When_InvalidCredentials() {
        // Crear usuario
        authService.createUser(registerDTO);

        // Datos de login incorrectos
        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName("testuser");
        validUser.setPassword("WrongPassword123!");

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authService.login(validUser));

        assertThat(exception.getMessage()).contains("Invalid credentials");
    }

    @Test
    @Transactional
    @Rollback
    void Should_ThrowResourceNotFoundException_When_UserNotFoundDuringLogin() {

        authService.createUser(registerDTO);
        // Intentar login con un usuario que no existe
        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName("nonexistentUser");
        validUser.setPassword("SecurePass123!");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> authService.login(validUser));

        assertThat(exception.getMessage()).contains("User not found");
    }

    @Test
    @Transactional
    @Rollback
    void Should_LoginSuccessfully_When_ValidCredentials() throws JsonProcessingException {
        // Crear usuario
        authService.createUser(registerDTO);

        // Datos de login correctos
        UserLoginDTO validUser = new UserLoginDTO();
        validUser.setUserName("testuser");
        validUser.setPassword("SecurePass123!");

        AccessTokenResponse logged = authService.login(validUser);

        // Verificar que el token de acceso no sea nulo ni vacío
        assertNotNull(logged);
        assertNotNull(logged.getToken());
    }
}
