package com.genuino.crm.client.service;

import com.genuino.crm.client.domain.ClientAccount;
import com.genuino.crm.client.dto.ClientAccountResponse;
import com.genuino.crm.client.dto.CreateClientAccountRequest;
import com.genuino.crm.client.infra.ClientAccountRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientAccountService {

    private final ClientAccountRepository clientAccountRepository;

    public ClientAccountService(ClientAccountRepository clientAccountRepository) {
        this.clientAccountRepository = clientAccountRepository;
    }

    @Transactional
    public ClientAccountResponse create(CreateClientAccountRequest request) {
        if (request.getLeadId() == null || request.getLeadId().isBlank()) {
            throw new IllegalArgumentException("El leadId es obligatorio");
        }

        if (request.getAcceptedProformaId() == null) {
            throw new IllegalArgumentException("La proforma aceptada es obligatoria");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo del cliente es obligatorio");
        }

        if (clientAccountRepository.existsByLeadId(request.getLeadId())) {
            throw new IllegalStateException("Este contacto ya tiene una cuenta cliente");
        }

        if (clientAccountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Ya existe un cliente con este correo");
        }

        ClientAccount client = new ClientAccount();
        client.setId(UUID.randomUUID());
        client.setLeadId(request.getLeadId());
        client.setAcceptedProformaId(request.getAcceptedProformaId());
        client.setCompanyName(request.getCompanyName());
        client.setContactName(request.getContactName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setUsername(
                request.getUsername() == null || request.getUsername().isBlank()
                        ? request.getEmail()
                        : request.getUsername()
        );
        client.setActive(true);
        client.setCreatedAt(LocalDateTime.now());

        clientAccountRepository.save(client);

        return toResponse(client);
    }

    private ClientAccountResponse toResponse(ClientAccount client) {
        ClientAccountResponse response = new ClientAccountResponse();

        response.setId(client.getId());
        response.setLeadId(client.getLeadId());
        response.setAcceptedProformaId(client.getAcceptedProformaId());
        response.setCompanyName(client.getCompanyName());
        response.setContactName(client.getContactName());
        response.setEmail(client.getEmail());
        response.setPhone(client.getPhone());
        response.setUsername(client.getUsername());
        response.setActive(client.isActive());
        response.setCreatedAt(client.getCreatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public boolean existsByLead(String leadId) {
        return clientAccountRepository.existsByLeadId(leadId);
    }
}