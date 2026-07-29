package com.genuino.crm.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genuino.crm.audit.AuditService;
import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.inbox.dto.WhapifyLeadRequest;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.opportunity.OpportunityService;
import com.genuino.crm.task.CommercialTaskService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.genuino.crm.security.SecurityUserService;

@Service
public class LeadInboxService {

    private final LeadInboxRepository repo;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final LeadAssignmentService leadAssignmentService;
    private final OpportunityService opportunityService;
    private final CommercialTaskService commercialTaskService;
    private final SecurityUserService securityUserService;

    public LeadInboxService(
            LeadInboxRepository repo,
            AuditService auditService,
            ObjectMapper objectMapper,
            LeadAssignmentService leadAssignmentService,
            OpportunityService opportunityService,
            CommercialTaskService commercialTaskService,
            SecurityUserService securityUserService
    )
    {
        this.repo = repo;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
        this.leadAssignmentService = leadAssignmentService;
        this.opportunityService = opportunityService;
        this.commercialTaskService = commercialTaskService;
        this.securityUserService = securityUserService;
    }

    @Transactional
    public LeadInbox receiveFromWhapify(WhapifyLeadRequest req) {

        LeadInbox lead = new LeadInbox();

        lead.id = "lead_" + UUID.randomUUID();
        lead.source = "WHAPIFY";
        lead.externalConversationId = req.externalConversationId();
        lead.externalContactId = req.externalContactId();
        lead.phone = normalizePhone(req.phone());
        lead.fullName = req.fullName();
        lead.messagePreview = req.messagePreview();
        lead.channel = req.channel() != null ? req.channel() : "WHATSAPP";
        lead.status = "NEW";
        lead.receivedAt = Instant.now();
        lead.createdAt = Instant.now();

        try {
            lead.payloadJson = objectMapper.writeValueAsString(req);
        } catch (Exception e) {
            lead.payloadJson = "{}";
        }

        Optional<LeadInbox> previousLead = repo.findFirstByPhoneOrderByReceivedAtDesc(lead.phone);

        if (previousLead.isPresent()
                && previousLead.get().assignedSellerId != null
                && !previousLead.get().assignedSellerId.isBlank()) {

            lead.assignedSellerId = previousLead.get().assignedSellerId;
            lead.assignmentRule = "KEEP_PREVIOUS_OWNER";

        } else {

            var assignment = leadAssignmentService.assignRandomSeller();

            lead.assignedSellerId = assignment.sellerId();
            lead.assignmentRule = assignment.rule();
        }

        LeadInbox saved = repo.save(lead);

        auditService.log(
                "CREATE",
                "LEAD_INBOX",
                saved.id,
                null,
                saved,
                "Ingreso desde Whapify",
                "SUCCESS",
                null
        );

        var opportunity = opportunityService.createFromLead(saved);

        commercialTaskService.createFollowUpPlan(
                saved.id,
                opportunity.id,
                saved.assignedSellerId
        );

        return saved;
    }



    @Transactional
public LeadInbox createManualLead(
        String fullName,
        String phone,
        String messagePreview,
        String assignedSellerId
) {
    String normalizedPhone = normalizePhone(phone);

   

    LeadInbox lead = new LeadInbox();

    lead.id = "lead_" + UUID.randomUUID();
    lead.source = "MANUAL";
    lead.phone = normalizedPhone;
    lead.fullName = fullName;
    lead.messagePreview = messagePreview;
    lead.channel = "CRM";
    lead.status = "NEW";
    lead.receivedAt = Instant.now();
    lead.createdAt = Instant.now();

    String currentUser = securityUserService.getCurrentUser();
    String currentRole = securityUserService.getHighestRole();

    if (assignedSellerId != null && !assignedSellerId.isBlank()) {

        lead.assignedSellerId = assignedSellerId;
        lead.assignmentRule = "MANUAL";

    } else if ("ADMIN".equals(currentRole)
            || "OWNER".equals(currentRole)
            || "GERENCIA".equals(currentRole)) {

        var assignment = leadAssignmentService.assignRandomSeller();

        lead.assignedSellerId = assignment.sellerId();
        lead.assignmentRule = assignment.rule();

    } else {

        lead.assignedSellerId = currentUser;
        lead.assignmentRule = "SELF";
    }

    LeadInbox saved = repo.save(lead);

    auditService.log(
            "CREATE",
            "LEAD_INBOX",
            saved.id,
            null,
            saved,
            "Ingreso manual desde CRM",
            "SUCCESS",
            null
    );

    var opportunity = opportunityService.createFromLead(saved);

    commercialTaskService.createFollowUpPlan(
            saved.id,
            opportunity.id,
            saved.assignedSellerId
    );

    return saved;
}



    private String normalizePhone(String phone) {

        if (phone == null) return null;

        String p = phone.trim();

        if (p.startsWith("591")) {
            p = "+" + p;
        }

        if (!p.startsWith("+")) {
            p = "+591" + p;
        }

        return p;
    }
}