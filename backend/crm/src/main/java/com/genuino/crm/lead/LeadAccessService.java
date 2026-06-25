package com.genuino.crm.lead;

import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.security.DataScopeService;
import org.springframework.stereotype.Service;

@Service
public class LeadAccessService {

    private final LeadInboxRepository repository;
    private final DataScopeService dataScopeService;

    public LeadAccessService(
            LeadInboxRepository repository,
            DataScopeService dataScopeService
    ) {
        this.repository = repository;
        this.dataScopeService = dataScopeService;
    }

    public LeadInbox getAuthorizedLead(String leadId) {

        LeadInbox lead = repository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado."));

        if (dataScopeService.onlyMine()
                && !dataScopeService.currentSeller().equals(lead.assignedSellerId)) {

            throw new RuntimeException("No tiene permisos para acceder a este lead.");
        }

        return lead;
    }
}