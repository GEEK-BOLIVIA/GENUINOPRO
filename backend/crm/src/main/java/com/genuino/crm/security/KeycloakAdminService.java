package com.genuino.crm.security;

import com.genuino.crm.admin.dto.AdminUserResponse;
import com.genuino.crm.admin.dto.CreateUserRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genuino.crm.admin.dto.ResetPasswordRequest;
import com.genuino.crm.admin.dto.UpdateUserRequest;
import java.util.List;

import com.genuino.crm.account.dto.MeResponse;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public MeResponse getMe(String userId) {
        UserRepresentation user = keycloak.realm(realm)
                .users()
                .get(userId)
                .toRepresentation();

        return new MeResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                getPrimaryRole(userId)
        );
    }

    public void changeMyPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(credential);
    }

    public AdminUserResponse createUser(CreateUserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());

        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("No se pudo crear el usuario en Keycloak. Status: " + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        assignRealmRole(userId, request.role());

        return new AdminUserResponse(
                userId,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                true,
                request.role()
        );
    }

    public List<AdminUserResponse> listUsers() {

        List<UserRepresentation> users = keycloak.realm(realm)
                .users()
                .list();

        return users.stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.isEnabled(),
                        getPrimaryRole(user.getId())
                ))
                .toList();
    }

    public List<AdminUserResponse> listActiveSellers() {
        return listUsers().stream()
                .filter(AdminUserResponse::enabled)
                .filter(user -> "VENDEDOR".equals(user.role()))
                .toList();
    }

    public AdminUserResponse updateUser(String userId, UpdateUserRequest request) {
        UserRepresentation user = keycloak.realm(realm)
                .users()
                .get(userId)
                .toRepresentation();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        keycloak.realm(realm)
                .users()
                .get(userId)
                .update(user);

        replaceRealmRole(userId, request.role());

        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                request.role()
        );
    }

    public void enableUser(String userId) {
        setUserEnabled(userId, true);
    }

    public void disableUser(String userId) {
        setUserEnabled(userId, false);
    }

    public void resetPassword(String userId, ResetPasswordRequest request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(credential);
    }

    private void setUserEnabled(String userId, boolean enabled) {
        UserRepresentation user = keycloak.realm(realm)
                .users()
                .get(userId)
                .toRepresentation();

        user.setEnabled(enabled);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .update(user);
    }

    private void replaceRealmRole(String userId, String newRoleName) {
        List<String> managedRoles = List.of(
                "ADMIN",
                "GERENCIA",
                "SUPERVISOR",
                "VENDEDOR",
                "CLIENTE"
        );

        List<RoleRepresentation> currentRoles = keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .filter(role -> managedRoles.contains(role.getName()))
                .toList();

        if (!currentRoles.isEmpty()) {
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .remove(currentRoles);
        }

        assignRealmRole(userId, newRoleName);
    }

    private void assignRealmRole(String userId, String roleName) {
        RoleRepresentation role = keycloak
                .realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    private String getPrimaryRole(String userId) {
        List<RoleRepresentation> roles = keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAll();

        return roles.stream()
                .map(RoleRepresentation::getName)
                .filter(role -> List.of("ADMIN", "GERENCIA", "SUPERVISOR", "VENDEDOR", "CLIENTE").contains(role))
                .findFirst()
                .orElse("SIN_ROL");
    }


}