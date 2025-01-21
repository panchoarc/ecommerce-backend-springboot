package com.buyit.ecommerce.util;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class KeycloakProvider {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Getter
    @Value("${keycloak.realm.name}")
    private String realmName;

    @Value("${keycloak.realm.master-realm}")
    private String realmMaster;

    @Value("${keycloak.admin.cli}")
    private String adminCli;

    @Value("${keycloak.admin.user-console}")
    private String userConsole;

    @Value("${keycloak.admin.password-console}")
    private String passwordConsole;

    @Value("${keycloak.admin.secret}")
    private String clientSecret;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmMaster)
                .clientId(adminCli)
                .username(userConsole)
                .password(passwordConsole)
                .clientSecret(clientSecret)
                .build();
    }

    @Bean
    public RealmResource realmResource() {
        return keycloak().realm(realmName);
    }

    @Bean
    public UsersResource usersResource() {
        return realmResource().users();
    }


    // Obtener los roles de un cliente específico
    public List<RoleRepresentation> getClientRoles() {
        return realmResource().clients().findByClientId(clientId).stream()
                .findFirst()
                .map(client -> realmResource().clients().get(client.getId()).roles().list())
                .orElse(Collections.emptyList());
    }

    public String getClientInternalId() {

        return realmResource().
                clients().
                findByClientId(clientId)
                .stream().
                findFirst().
                orElseThrow(() -> new RuntimeException("Client with clientId '" + clientId + "' not found"))
                .getId();
    }

    // Obtener los usuarios del realm



    public String getClientId() {
        return realmResource().clients().findByClientId(clientId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client with clientId '" + clientId + "' not found"))
                .getClientId();
    }

    // Retrieve client secret
    public String getClientSecret() {
        ClientRepresentation client = realmResource().clients().findByClientId(clientId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client with clientId '" + clientId + "' not found"));

        return realmResource().clients().get(client.getId()).getSecret().getValue();
    }



    public String getAuthServerUrl() {
        return serverUrl;
    }

}