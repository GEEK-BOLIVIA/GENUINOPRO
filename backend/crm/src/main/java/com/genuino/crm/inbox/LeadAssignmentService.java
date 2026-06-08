package com.genuino.crm.inbox;

import com.genuino.crm.admin.dto.AdminUserResponse;
import com.genuino.crm.inbox.domain.LeadAssignmentPointer;
import com.genuino.crm.inbox.infra.LeadAssignmentPointerRepository;
import com.genuino.crm.security.KeycloakAdminService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class LeadAssignmentService {

    private static final String POINTER_ID = "DEFAULT";

    private final KeycloakAdminService keycloakAdminService;
    private final LeadAssignmentPointerRepository pointerRepository;

    public LeadAssignmentService(
            KeycloakAdminService keycloakAdminService,
            LeadAssignmentPointerRepository pointerRepository
    ) {
        this.keycloakAdminService = keycloakAdminService;
        this.pointerRepository = pointerRepository;
    }

    public AssignmentResult assignRandomSeller() {
        List<AdminUserResponse> sellers = keycloakAdminService.listActiveSellers()
                .stream()
                .sorted(Comparator.comparing(AdminUserResponse::username))
                .toList();

        if (sellers.isEmpty()) {
            return new AssignmentResult("admin", "NO_ACTIVE_SELLERS_FALLBACK_ADMIN");
        }

        LeadAssignmentPointer pointer = pointerRepository
                .findById(POINTER_ID)
                .orElseGet(() -> {
                    LeadAssignmentPointer created = new LeadAssignmentPointer();
                    created.id = POINTER_ID;
                    created.lastAssignedSellerId = null;
                    created.updatedAt = Instant.now();
                    return created;
                });

        int nextIndex = 0;

        if (pointer.lastAssignedSellerId != null) {
            for (int i = 0; i < sellers.size(); i++) {
                if (sellers.get(i).username().equals(pointer.lastAssignedSellerId)) {
                    nextIndex = (i + 1) % sellers.size();
                    break;
                }
            }
        }

        AdminUserResponse selected = sellers.get(nextIndex);

        pointer.lastAssignedSellerId = selected.username();
        pointer.updatedAt = Instant.now();

        pointerRepository.save(pointer);

        return new AssignmentResult(selected.username(), "ROUND_ROBIN_KEYCLOAK");
    }

    public record AssignmentResult(String sellerId, String rule) {}
}