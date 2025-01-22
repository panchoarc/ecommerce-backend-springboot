package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Override
    public User getUserByKeycloakId(String keycloakId) {
        return usersRepository.findByKeycloakUserId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public String extractKeycloakIdFromUser(Jwt user) {
        return (String) user.getClaims().get("sub");
    }

    @Override
    public void saveUserToDatabase(UserRegisterDTO userDTO, String keycloakId) {
        User newUser = new User();
        newUser.setEmail(userDTO.getEmail());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setUserName(userDTO.getUserName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setKeycloakUserId(keycloakId);
        usersRepository.save(newUser);
    }

    @Override
    public boolean userExistsInDatabase(String email, String username) {
        return usersRepository.findByEmailOrUserName(email, username).isPresent();

    }


}
