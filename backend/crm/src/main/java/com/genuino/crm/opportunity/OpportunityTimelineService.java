package com.genuino.crm.opportunity;

import com.genuino.crm.audit.infra.AuditEventRepository;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.OpportunityTimelineItemResponse;
import com.genuino.crm.opportunity.dto.OpportunityTimelineResponse;
import com.genuino.crm.opportunity.infra.OpportunityActivityRepository;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genuino.crm.quoting.common.infra.TypedProformaRepository;

import java.util.*;

@Service
public class OpportunityTimelineService {

    private final OpportunityRepository opportunityRepository;
    private final LeadInboxRepository leadInboxRepository;
    private final ProformaRepository proformaRepository;
    private final AuditEventRepository auditEventRepository;
    private final OpportunityActivityRepository opportunityActivityRepository;
    private final TypedProformaRepository typedProformaRepository;

    public OpportunityTimelineService(
            OpportunityRepository opportunityRepository,
            LeadInboxRepository leadInboxRepository,
            ProformaRepository proformaRepository,
            AuditEventRepository auditEventRepository,
            OpportunityActivityRepository opportunityActivityRepository,
            TypedProformaRepository typedProformaRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.leadInboxRepository = leadInboxRepository;
        this.proformaRepository = proformaRepository;
        this.auditEventRepository = auditEventRepository;
        this.opportunityActivityRepository = opportunityActivityRepository;
        this.typedProformaRepository = typedProformaRepository;
    }

    @Transactional(readOnly = true)
    public OpportunityTimelineResponse getTimeline(String opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElseThrow();

        List<Map<String, Object>> events = new ArrayList<>();

        if (opportunity.leadInboxId != null) {
            leadInboxRepository.findById(opportunity.leadInboxId).ifPresent(lead -> {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("type", "LEAD_CREATED");
                e.put("timestamp", lead.receivedAt);
                e.put("source", lead.source);
                e.put("phone", lead.phone);
                e.put("fullName", lead.fullName);
                e.put("assignedSellerId", lead.assignedSellerId);
                e.put("messagePreview", lead.messagePreview);
                events.add(e);
            });
        }

        Map<String, Object> oppCreated = new LinkedHashMap<>();
        oppCreated.put("type", "OPPORTUNITY_CREATED");
        oppCreated.put("timestamp", opportunity.createdAt);
        oppCreated.put("stage", opportunity.stage);
        oppCreated.put("ownerUserId", opportunity.ownerUserId);
        oppCreated.put("title", opportunity.title);
        events.add(oppCreated);

        var opportunityAudit = auditEventRepository
                .findByEntityTypeAndEntityIdOrderByTsAsc("OPPORTUNITY", opportunityId);

        for (var audit : opportunityAudit) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", audit.action);
            e.put("timestamp", audit.ts);
            e.put("actorUserId", audit.actorUserId);
            e.put("reason", audit.reason);
            e.put("result", audit.result);
            events.add(e);
        }

        var proformas = proformaRepository.findByOpportunityId(opportunityId);

        for (var proforma : proformas) {
            Map<String, Object> created = new LinkedHashMap<>();
            created.put("type", "PROFORMA_CREATED");
            created.put("timestamp", proforma.createdAt);
            created.put("proformaId", proforma.id);
            created.put("status", proforma.status);
            created.put("total", proforma.total);
            created.put("currency", proforma.currency);
            events.add(created);

            var proformaAudit = auditEventRepository
                    .findByEntityTypeAndEntityIdOrderByTsAsc("PROFORMA", proforma.id);

            for (var audit : proformaAudit) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("type", "PROFORMA_" + audit.action);
                e.put("timestamp", audit.ts);
                e.put("proformaId", proforma.id);
                e.put("actorUserId", audit.actorUserId);
                e.put("reason", audit.reason);
                e.put("result", audit.result);
                events.add(e);
            }
        }

        var typedProformas = typedProformaRepository
                    .findByOpportunityIdOrderByCreatedAtDesc(opportunityId);

            for (var proforma : typedProformas) {

                Map<String, Object> created = new LinkedHashMap<>();
                created.put("type", "PROFORMA_CREATED");
                created.put("timestamp", proforma.getCreatedAt());
                created.put("proformaId", proforma.getId());
                created.put("status", proforma.getStatus());
                created.put("total", proforma.getTotal());
                created.put("currency", proforma.getCurrency());
                events.add(created);

                var audits = auditEventRepository
                        .findByEntityTypeAndEntityIdOrderByTsAsc(
                                "PROFORMA",
                                proforma.getId().toString()
                        );

                for (var audit : audits) {
                    Map<String, Object> e = new LinkedHashMap<>();
                    e.put("type", "PROFORMA_" + audit.action);
                    e.put("timestamp", audit.ts);
                    e.put("proformaId", proforma.getId());
                    e.put("actorUserId", audit.actorUserId);
                    e.put("reason", audit.reason);
                    e.put("result", audit.result);
                    events.add(e);
                }
            }

        events.sort(Comparator.comparing(e -> String.valueOf(e.get("timestamp"))));

        List<OpportunityTimelineItemResponse> activities = opportunityActivityRepository
                .findByOpportunityIdOrderByActivityDateDesc(opportunityId)
                .stream()
                .map(activity -> new OpportunityTimelineItemResponse(
                        activity.getId(),
                        activity.getOpportunityId(),
                        activity.getType(),
                        activity.getTitle(),
                        activity.getDescription(),
                        activity.getActivityDate(),
                        activity.getSource(),
                        activity.getCreatedBy()
                ))
                .toList();

        OpportunityTimelineResponse response = new OpportunityTimelineResponse(
                opportunity.id,
                opportunity.title,
                opportunity.stage,
                opportunity.ownerUserId,
                events
        );

        response.setActivities(activities);

        return response;
    }
}