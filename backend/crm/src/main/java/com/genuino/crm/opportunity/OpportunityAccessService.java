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
                .orElseThrow(() ->
                        new RuntimeException("Oportunidad no encontrada.")
                );

        String currentSeller = normalize(dataScopeService.currentSeller());
        String ownerUserId = normalize(opportunity.ownerUserId);

        System.out.println("================================");
        System.out.println("USER LOGIN : " + currentSeller);
        System.out.println("OWNER USER : " + ownerUserId);
        System.out.println("OPPORTUNITY: " + opportunity.id);
        System.out.println("================================");

        if (
            dataScopeService.onlyMine()
            && (
                currentSeller == null
                || ownerUserId == null
                || !currentSeller.equalsIgnoreCase(ownerUserId)
            )
        ) {
            throw new RuntimeException(
                    "No tiene permisos para acceder a esta oportunidad."
            );
        }

        return opportunity;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}