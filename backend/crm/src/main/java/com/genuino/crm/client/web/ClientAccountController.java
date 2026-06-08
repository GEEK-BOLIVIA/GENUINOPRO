package com.genuino.crm.client.web;

import com.genuino.crm.client.dto.ClientAccountResponse;
import com.genuino.crm.client.dto.CreateClientAccountRequest;
import com.genuino.crm.client.service.ClientAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientAccountController {

    private final ClientAccountService clientAccountService;

    public ClientAccountController(ClientAccountService clientAccountService) {
        this.clientAccountService = clientAccountService;
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PostMapping
    public ResponseEntity<ClientAccountResponse> create(
            @RequestBody CreateClientAccountRequest request
    ) {
        return ResponseEntity.ok(clientAccountService.create(request));
    }

    @GetMapping("/exists/{leadId}")
    public ResponseEntity<Boolean> exists(
            @PathVariable String leadId
    ) {
        return ResponseEntity.ok(
                clientAccountService.existsByLead(leadId)
        );
    }
}