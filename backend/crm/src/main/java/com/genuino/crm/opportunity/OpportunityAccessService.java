package com.genuino.crm.opportunity;

import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.security.DataScopeService;
import org.springframework.stereotype.Service;

@Service
public class OpportunityAccessService {

    private final OpportunityRepository repository;
    private final DataScopeService dataScopeService;

    public OpportunityAccessService(
            OpportunityRepository repository,
            DataScopeService dataScopeService
    ) {
        this.repository = repository;
        this.dataScopeService = dataScopeService;
    }

    public Opportunity getAuthorizedOpportunity(String id) {

        Opportunity opportunity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada."));

        if (dataScopeService.onlyMine()
                && !dataScopeService.currentSeller().equals(opportunity.ownerUserId)) {

            throw new RuntimeException("No tiene permisos para acceder a esta oportunidad.");
        }

        return opportunity;
    }
}