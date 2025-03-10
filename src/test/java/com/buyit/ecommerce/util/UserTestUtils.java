package com.buyit.ecommerce.util;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserTestUtils {

    private final KeycloakService keycloakService;

    private final UsersRepository usersRepository;

    private final AuthService authService;

    private final TokenExtractor tokenExtractor;

    public String getAdminUserToken() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("AdminFirstname");
        userRegisterDTO.setLastName("AdminLastname");
        userRegisterDTO.setRole("admin");
        userRegisterDTO.setEmail("admintest" + System.currentTimeMillis() + "@example.com");
        userRegisterDTO.setUserName("admintest" + System.currentTimeMillis());
        userRegisterDTO.setPassword("SecurePass123!");

        authService.createUser(userRegisterDTO);

        return tokenExtractor.extractTokenFromUser(userRegisterDTO.getUserName(), userRegisterDTO.getPassword());
    }


    public String getUserToken() throws JsonProcessingException {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("UserFirstname");
        userRegisterDTO.setLastName("UserLastname");
        userRegisterDTO.setEmail("usertest" + System.currentTimeMillis() + "@example.com");
        userRegisterDTO.setUserName("usertest" + System.currentTimeMillis());
        userRegisterDTO.setPassword("SecurePass123!");

        authService.createUser(userRegisterDTO);

        return tokenExtractor.extractTokenFromUser(userRegisterDTO.getUserName(), userRegisterDTO.getPassword());
    }


    public void cleanUsers() {
        List<User> users = usersRepository.findAll();

        log.info("USERS IN CATEGORY: {}", users.size());
        if (!users.isEmpty()) {
            for (User user : users) {
                keycloakService.deleteUserFromKeycloak(user.getKeycloakUserId());
            }
        }
        usersRepository.deleteAll();

    }


}
