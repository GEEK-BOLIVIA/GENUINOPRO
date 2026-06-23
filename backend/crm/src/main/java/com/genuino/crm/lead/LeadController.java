package com.genuino.crm.lead;

import com.genuino.crm.inbox.LeadInboxService;
import com.genuino.crm.commercialsummary.CommercialSummaryService;
import com.genuino.crm.commercialsummary.dto.CommercialSummaryResponse;
import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.lead.dto.CreateManualLeadRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.genuino.crm.activity.LeadActivity;
import com.genuino.crm.activity.LeadActivityService;
import com.genuino.crm.opportunity.OpportunityTimelineService;
import com.genuino.crm.opportunity.infra.OpportunityRepository;

@RestController
public class LeadController {

    private final LeadInboxRepository leadInboxRepository;
    private final LeadInboxService leadInboxService;
    private final LeadActivityService leadActivityService;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityTimelineService opportunityTimelineService;
    private final CommercialSummaryService commercialSummaryService;

    public LeadController(
            LeadInboxRepository leadInboxRepository,
            LeadInboxService leadInboxService,
            LeadActivityService leadActivityService,
            OpportunityRepository opportunityRepository,
            OpportunityTimelineService opportunityTimelineService,
            CommercialSummaryService commercialSummaryService
    ) {
        this.leadInboxRepository = leadInboxRepository;
        this.leadInboxService = leadInboxService;
        this.leadActivityService = leadActivityService;
        this.opportunityRepository = opportunityRepository;
        this.opportunityTimelineService = opportunityTimelineService;
        this.commercialSummaryService = commercialSummaryService;
    }

    @GetMapping("/api/leads")
    public List<Map<String, Object>> getLeads() {

        return leadInboxRepository.findAll()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        lead -> lead.phone != null ? lead.phone : lead.id,
                        lead -> lead,
                        (a, b) -> {
                        if (a.receivedAt == null) return b;
                        if (b.receivedAt == null) return a;
                        return a.receivedAt.isAfter(b.receivedAt) ? a : b;
                        }
                ))
                .values()
                .stream()
                .sorted((a, b) -> {
                if (a.receivedAt == null) return 1;
                if (b.receivedAt == null) return -1;
                return b.receivedAt.compareTo(a.receivedAt);
                })
                .map(this::toLeadResponse)
                .toList();
    }

        @GetMapping("/api/leads/{leadId}")
        public Map<String, Object> getLeadById(
                @PathVariable String leadId
        ) {

        LeadInbox lead = leadInboxRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado"));

        return toLeadResponse(lead);
        }

    @GetMapping("/api/leads/{leadId}/commercial-summary")
    public CommercialSummaryResponse getCommercialSummary(
            @PathVariable String leadId
    ) {
        return commercialSummaryService.getByLeadId(leadId);
    }

    @GetMapping("/api/leads/{leadId}/activities")
    public List<LeadActivity> getLeadActivities(
            @PathVariable String leadId
    ) {
        return leadActivityService.getLeadActivities(leadId);
    }

        @GetMapping("/api/leads/{leadId}/commercial-timeline")
        public Object getLeadCommercialTimeline(
                @PathVariable String leadId
        ) {

        var opportunity = opportunityRepository
                .findByLeadInboxId(leadId);

        if (opportunity.isEmpty()) {
                return List.of();
        }

        return opportunityTimelineService.getTimeline(
                opportunity.get().id
        );
        }

    @PostMapping("/api/leads/{leadId}/activities")
    public LeadActivity createLeadActivity(
            @PathVariable String leadId,
            @RequestBody Map<String, String> body
    ) {
        return leadActivityService.createActivity(
                leadId,
                body.getOrDefault("type", "NOTE"),
                body.getOrDefault("description", ""),
                body.getOrDefault("createdBy", "admin")
        );
    }
    @PostMapping("/api/leads")
    public Map<String, Object> createManualLead(
            @RequestBody CreateManualLeadRequest request
    ) {
        LeadInbox saved = leadInboxService.createManualLead(
                request.fullName(),
                request.phone(),
                request.messagePreview(),
                request.assignedSellerId()
        );

        return toLeadResponse(saved);
    }

    @PatchMapping("/api/leads/{leadId}/status")
    public Map<String, Object> updateLeadStatus(
            @PathVariable String leadId,
            @RequestBody Map<String, String> body
    ) {
        var lead = leadInboxRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado"));

        lead.status = body.getOrDefault("status", lead.status);

        var saved = leadInboxRepository.save(lead);

        return toLeadResponse(saved);
    }


    private Map<String, Object> toLeadResponse(LeadInbox lead) {

        Map<String, Object> response = new java.util.HashMap<>();

        response.put("id", lead.id);
        response.put("company", lead.fullName != null ? lead.fullName : "Lead sin nombre");
        response.put("contact", lead.fullName != null ? lead.fullName : "Sin contacto");
        response.put("phone", lead.phone != null ? lead.phone : "-");
        response.put("email", "-");
        response.put("status", lead.status != null ? lead.status : "NEW");
        response.put("priority", "Media");
        response.put("owner", lead.assignedSellerId != null ? lead.assignedSellerId : "Sin asignar");
        response.put("source", lead.source != null ? lead.source : "-");
        response.put("channel", lead.channel != null ? lead.channel : "-");
        response.put("messagePreview", lead.messagePreview != null ? lead.messagePreview : "-");
        response.put("createdAt", lead.createdAt != null ? lead.createdAt.toString() : "-");

        return response;
    }
}