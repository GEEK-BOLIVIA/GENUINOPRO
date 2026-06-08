package com.genuino.crm.opportunity.controller;

import com.genuino.crm.opportunity.dto.CreateOpportunityActivityRequest;
import com.genuino.crm.opportunity.dto.OpportunityTimelineItemResponse;
import com.genuino.crm.opportunity.service.OpportunityActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityActivityController {

    private final OpportunityActivityService opportunityActivityService;

    public OpportunityActivityController(OpportunityActivityService opportunityActivityService) {
        this.opportunityActivityService = opportunityActivityService;
    }

    @PostMapping("/{leadId}/timeline")
    @ResponseStatus(HttpStatus.CREATED)
    public OpportunityTimelineItemResponse createManualActivity(
            @PathVariable String leadId,
            @Valid @RequestBody CreateOpportunityActivityRequest request,
            Principal principal
    ) {
        String currentUser = principal != null ? principal.getName() : "system";
        return opportunityActivityService.createManualActivity(leadId, request, currentUser);
    }
}