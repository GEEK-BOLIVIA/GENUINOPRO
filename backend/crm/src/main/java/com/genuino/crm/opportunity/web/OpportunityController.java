package com.genuino.crm.opportunity.web;

import com.genuino.crm.opportunity.OpportunityConversionService;
import com.genuino.crm.opportunity.OpportunityPipelineService;
import com.genuino.crm.opportunity.OpportunityTimelineService;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.OpportunityCloseLostRequest;
import com.genuino.crm.opportunity.dto.OpportunityConvertToCustomerResponse;
import com.genuino.crm.opportunity.dto.OpportunityStagePatchRequest;
import com.genuino.crm.opportunity.dto.OpportunityTimelineResponse;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.domain.Proforma;
import com.genuino.crm.quoting.infra.ProformaRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

import com.genuino.crm.security.DataScopeService;
import com.genuino.crm.opportunity.OpportunityAccessService;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityRepository repo;
    private final OpportunityPipelineService pipelineService;
    private final ProformaRepository proformaRepository;
    private final OpportunityTimelineService timelineService;
    private final OpportunityConversionService conversionService;

    private final TypedProformaRepository typedProformaRepository;

    private final DataScopeService dataScopeService;
    private final OpportunityAccessService opportunityAccessService;


    
    public OpportunityController(
            OpportunityRepository repo,
            OpportunityPipelineService pipelineService,
            ProformaRepository proformaRepository,
            OpportunityTimelineService timelineService,
            OpportunityConversionService conversionService,
            TypedProformaRepository typedProformaRepository,
            DataScopeService dataScopeService,
            OpportunityAccessService opportunityAccessService
    ) {
        this.repo = repo;
        this.pipelineService = pipelineService;
        this.proformaRepository = proformaRepository;
        this.timelineService = timelineService;
        this.conversionService = conversionService;
        this.typedProformaRepository = typedProformaRepository;
        this.dataScopeService = dataScopeService;
        this.opportunityAccessService = opportunityAccessService;
    }


    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping
    public List<Opportunity> list() {

        if (dataScopeService.canSeeEverything()) {
            return repo.findAll();
        }

        return repo.findByOwnerUserIdOrderByUpdatedAtDesc(
                dataScopeService.currentSeller()
        );
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}")
    public Opportunity getById(@PathVariable String id) {
        return opportunityAccessService.getAuthorizedOpportunity(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PatchMapping("/{id}/stage")
    public Opportunity changeStage(
            @PathVariable String id,
            @Valid @RequestBody OpportunityStagePatchRequest req
    ) {
    opportunityAccessService.getAuthorizedOpportunity(id);

        return pipelineService.changeStage(id, req);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PostMapping("/{id}/close-lost")
    public Opportunity closeLost(
            @PathVariable String id,
            @Valid @RequestBody OpportunityCloseLostRequest req
    ) {
    opportunityAccessService.getAuthorizedOpportunity(id);

    return pipelineService.closeLost(id, req);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/proformas")
    public List<Proforma> proformasByOpportunity(@PathVariable String id) {
       opportunityAccessService.getAuthorizedOpportunity(id);

        return proformaRepository.findByOpportunityId(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/typed-proformas")
    public List<TypedProforma> typedProformas(
            @PathVariable String id
    ) {
    opportunityAccessService.getAuthorizedOpportunity(id);

    return typedProformaRepository
            .findByOpportunityIdOrderByCreatedAtDesc(id);
        }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/timeline")
    public OpportunityTimelineResponse timeline(@PathVariable String id) {

        opportunityAccessService.getAuthorizedOpportunity(id);

        return timelineService.getTimeline(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/dashboard")
    public Map<String, Object> dashboard(
            @PathVariable String id
    ) {

        Opportunity opportunity =
            opportunityAccessService.getAuthorizedOpportunity(id);

        var typedProformas =
                typedProformaRepository
                        .findByOpportunityIdOrderByCreatedAtDesc(id);

        Map<String, Object> response = new HashMap<>();

        response.put("opportunityId", opportunity.id);
        response.put("title", opportunity.title);
        response.put("stage", opportunity.stage);
        response.put("ownerUserId", opportunity.ownerUserId);
        response.put("proformaCount", typedProformas.size());

        if (!typedProformas.isEmpty()) {
            response.put(
                    "lastProformaDate",
                    typedProformas.get(0).getCreatedAt()
            );
        }

        return response;
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PostMapping("/{id}/convert-to-customer")
    public OpportunityConvertToCustomerResponse convertToCustomer(@PathVariable String id) {
    opportunityAccessService.getAuthorizedOpportunity(id);

    return conversionService.convertToCustomer(id);
    }

}