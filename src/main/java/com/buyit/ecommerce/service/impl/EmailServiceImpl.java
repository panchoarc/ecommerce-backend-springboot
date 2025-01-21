package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.service.EmailService;
import com.buyit.ecommerce.util.KeycloakProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    public final KeycloakProvider keycloakProvider;


    @Async("taskExecutor")
    @Override
    public void sendKeycloakVerifyEmail(String keycloakId) {
        keycloakProvider.usersResource().get(keycloakId).sendVerifyEmail();
    }
}
