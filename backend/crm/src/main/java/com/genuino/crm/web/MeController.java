package com.genuino.crm.web;

import com.genuino.crm.account.dto.ChangeMyPasswordRequest;
import com.genuino.crm.security.KeycloakAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

    private final KeycloakAdminService keycloakAdminService;

    public MeController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {

        var authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();

            return Map.of(
                    "sub", jwt.getSubject(),
                    "preferred_username", jwt.getClaimAsString("preferred_username"),
                    "authorities", authorities
            );
        }

        return Map.of(
                "user", auth.getName(),
                "authorities", authorities
        );
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangeMyPasswordRequest request
    ) {

        if (auth instanceof JwtAuthenticationToken jwtAuth) {

            String userId = jwtAuth.getToken().getSubject();

            keycloakAdminService.changeMyPassword(
                    userId,
                    request.newPassword()
            );
        }

        return ResponseEntity.noContent().build();
    }
}