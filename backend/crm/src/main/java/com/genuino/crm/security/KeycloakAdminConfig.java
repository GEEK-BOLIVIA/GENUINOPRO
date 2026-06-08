package com.genuino.crm.security;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloakAdminClient(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.admin.realm}") String adminRealm,
            @Value("${keycloak.admin.username}") String username,
            @Value("${keycloak.admin.password}") String password
    ) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId("admin-cli")
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}