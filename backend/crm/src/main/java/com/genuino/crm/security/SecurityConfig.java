package com.genuino.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter()
        );
        http
            .cors(cors -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/integrations/whapify/**").permitAll()

                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/parameters/**").hasAnyRole("ADMIN", "GERENCIA")

                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "GERENCIA", "SUPERVISOR")

                .requestMatchers("/api/leads/**").hasAnyRole("ADMIN", "GERENCIA", "SUPERVISOR", "VENDEDOR")
                .requestMatchers("/api/opportunities/**").hasAnyRole("ADMIN", "GERENCIA", "SUPERVISOR", "VENDEDOR")
                .requestMatchers("/api/commercial-tasks/**").hasAnyRole("ADMIN", "GERENCIA", "SUPERVISOR", "VENDEDOR")
                .requestMatchers("/api/typed-proformas/**").hasAnyRole("ADMIN", "GERENCIA", "SUPERVISOR", "VENDEDOR")

                .requestMatchers("/api/me/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            );

        return http.build();
    }
}