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

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityRepository repo;
    private final OpportunityPipelineService pipelineService;
    private final ProformaRepository proformaRepository;
    private final OpportunityTimelineService timelineService;
    private final OpportunityConversionService conversionService;

    private final TypedProformaRepository typedProformaRepository;


    
    public OpportunityController(
            OpportunityRepository repo,
            OpportunityPipelineService pipelineService,
            ProformaRepository proformaRepository,
            OpportunityTimelineService timelineService,
            OpportunityConversionService conversionService,
            TypedProformaRepository typedProformaRepository
    ) {
        this.repo = repo;
        this.pipelineService = pipelineService;
        this.proformaRepository = proformaRepository;
        this.timelineService = timelineService;
        this.conversionService = conversionService;
        this.typedProformaRepository = typedProformaRepository;
    }


    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping
    public List<Opportunity> list() {
        return repo.findAll();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}")
    public Opportunity getById(@PathVariable String id) {
        return repo.findById(id).orElseThrow();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PatchMapping("/{id}/stage")
    public Opportunity changeStage(
            @PathVariable String id,
            @Valid @RequestBody OpportunityStagePatchRequest req
    ) {
        return pipelineService.changeStage(id, req);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PostMapping("/{id}/close-lost")
    public Opportunity closeLost(
            @PathVariable String id,
            @Valid @RequestBody OpportunityCloseLostRequest req
    ) {
        return pipelineService.closeLost(id, req);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/proformas")
    public List<Proforma> proformasByOpportunity(@PathVariable String id) {
        return proformaRepository.findByOpportunityId(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/typed-proformas")
    public List<TypedProforma> typedProformas(
            @PathVariable String id
    ) {
        return typedProformaRepository
                .findByOpportunityIdOrderByCreatedAtDesc(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/timeline")
    public OpportunityTimelineResponse timeline(@PathVariable String id) {
        return timelineService.getTimeline(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}/dashboard")
    public Map<String, Object> dashboard(
            @PathVariable String id
    ) {

        Opportunity opportunity =
                repo.findById(id).orElseThrow();

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
        return conversionService.convertToCustomer(id);
    }

}