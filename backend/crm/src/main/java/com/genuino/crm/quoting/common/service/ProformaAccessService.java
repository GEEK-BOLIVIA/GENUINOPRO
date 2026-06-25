package com.genuino.crm.quoting.common.service;

import com.genuino.crm.opportunity.OpportunityAccessService;
import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProformaAccessService {

    private final TypedProformaRepository repository;
    private final OpportunityAccessService opportunityAccessService;

    public ProformaAccessService(
            TypedProformaRepository repository,
            OpportunityAccessService opportunityAccessService
    ) {
        this.repository = repository;
        this.opportunityAccessService = opportunityAccessService;
    }

    public TypedProforma getAuthorizedProforma(UUID id) {

        TypedProforma proforma = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));

        opportunityAccessService.getAuthorizedOpportunity(
                proforma.getOpportunityId()
        );

        return proforma;
    }
}