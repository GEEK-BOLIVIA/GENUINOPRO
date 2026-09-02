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

        String roleName = request.role().trim().toUpperCase();

        List<String> allowedRoles = List.of(
                "ADMIN",
                "GERENCIA",
                "SUPERVISOR",
                "VENDEDOR",
                "CLIENTE"
        );

        if (!allowedRoles.contains(roleName)) {
                throw new RuntimeException(
                        "Rol no permitido: " + roleName
                );
        }

        // Verificamos que el rol exista ANTES de crear el usuario.
        RoleRepresentation role;

        try {
                role = keycloak
                        .realm(realm)
                        .roles()
                        .get(roleName)
                        .toRepresentation();
        } catch (Exception e) {
                throw new RuntimeException(
                        "El rol " + roleName + " no existe en Keycloak.",
                        e
                );
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());

        user.setCredentials(List.of(credential));

        Response response = keycloak
                .realm(realm)
                .users()
                .create(user);

        if (response.getStatus() != 201) {

                int status = response.getStatus();
                response.close();

                throw new RuntimeException(
                        "No se pudo crear el usuario en Keycloak. Status: " + status
                );
        }

        String userId = response
                .getLocation()
                .getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        response.close();

        try {

                keycloak.realm(realm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(List.of(role));

        } catch (Exception e) {

                // Rollback: no dejamos usuarios creados sin rol.
                try {
                keycloak.realm(realm)
                        .users()
                        .delete(userId);
                } catch (Exception ignored) {
                }

                throw new RuntimeException(
                        "El usuario fue creado pero no se pudo asignar el rol "
                                + roleName
                                + ". El usuario fue eliminado automáticamente.",
                        e
                );
        }

        return new AdminUserResponse(
                userId,
                request.username().trim(),
                request.email().trim(),
                request.firstName().trim(),
                request.lastName().trim(),
                true,
                roleName
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