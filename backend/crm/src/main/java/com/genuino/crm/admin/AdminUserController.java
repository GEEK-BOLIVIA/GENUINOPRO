package com.genuino.crm.admin;

import com.genuino.crm.admin.dto.AdminUserResponse;
import com.genuino.crm.admin.dto.CreateUserRequest;
import com.genuino.crm.admin.dto.ResetPasswordRequest;
import com.genuino.crm.admin.dto.UpdateUserRequest;
import com.genuino.crm.security.KeycloakAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final KeycloakAdminService keycloakAdminService;

    public AdminUserController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> listUsers() {
        return ResponseEntity.ok(keycloakAdminService.listUsers());
    }

    @GetMapping("/assignable")
    public ResponseEntity<List<AdminUserResponse>> listAssignableUsers() {
        return ResponseEntity.ok(
                keycloakAdminService.listUsers()
                        .stream()
                        .filter(AdminUserResponse::enabled)
                        .filter(user ->
                                List.of(
                                        "ADMIN",
                                        "GERENCIA",
                                        "SUPERVISOR",
                                        "VENDEDOR"
                                ).contains(user.role())
                        )
                        .toList()
        );
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<AdminUserResponse>> listSellers() {
        return ResponseEntity.ok(
                keycloakAdminService.listActiveSellers()
        );
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(keycloakAdminService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(keycloakAdminService.updateUser(id, request));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable String id) {
        keycloakAdminService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable String id) {
        keycloakAdminService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        keycloakAdminService.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }
}