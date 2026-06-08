package com.genuino.crm.opportunity;

import com.genuino.crm.audit.AuditService;
import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OpportunityService {

    private final OpportunityRepository repo;
    private final AuditService auditService;

    public OpportunityService(OpportunityRepository repo, AuditService auditService) {
        this.repo = repo;
        this.auditService = auditService;
    }

    @Transactional
    public Opportunity createFromLead(LeadInbox lead) {
        Opportunity o = new Opportunity();
        o.id = "opp_" + UUID.randomUUID();
        o.customerId = null;
        o.leadInboxId = lead.id;
        o.title = lead.fullName != null && !lead.fullName.isBlank()
                ? "Lead WhatsApp - " + lead.fullName
                : "Lead WhatsApp - " + lead.phone;
        o.stage = "NUEVO";
        o.source = "WHAPIFY";
        o.ownerUserId = lead.assignedSellerId;
        o.notes = lead.messagePreview;

        Opportunity saved = repo.save(o);
        auditService.log("CREATE", "OPPORTUNITY", saved.id, null, saved, "Creada desde lead Whapify", "SUCCESS", null);
        return saved;
    }
}