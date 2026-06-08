package com.genuino.crm.opportunity;

import com.genuino.crm.audit.AuditService;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.OpportunityCloseLostRequest;
import com.genuino.crm.opportunity.dto.OpportunityStagePatchRequest;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
public class OpportunityPipelineService {

    private static final Set<String> ALLOWED_STAGES = Set.of(
            "NUEVO",
            "CONTACTADO",
            "SEGUIMIENTO",
            "REQUERIMIENTO_COMPLETO",
            "PROFORMA_GENERADA",
            "APROBACION_INTERNA",
            "APROBACION_CLIENTE",
            "CLIENTE",
            "PERDIDO"
    );

    private final OpportunityRepository repo;
    private final AuditService auditService;

    public OpportunityPipelineService(
            OpportunityRepository repo,
            AuditService auditService
    ) {
        this.repo = repo;
        this.auditService = auditService;
    }

    @Transactional
    public Opportunity changeStage(String id, OpportunityStagePatchRequest req) {
        if (req == null || req.stage() == null || req.stage().isBlank()) {
            throw new IllegalArgumentException("stage is required");
        }

        Opportunity o = repo.findById(id).orElseThrow();

        String newStage = req.stage().trim().toUpperCase();
        if (!ALLOWED_STAGES.contains(newStage)) {
            throw new IllegalArgumentException("Invalid stage");
        }

        Opportunity before = cloneOpportunity(o);

        o.stage = newStage;
        o.updatedAt = Instant.now();

        Opportunity saved = repo.save(o);

        String reasonSafe = (req.reason() != null && !req.reason().isBlank())
                ? req.reason()
                : "Cambio de etapa";

        try {
            auditService.log(
                    "CHANGE_STAGE",
                    "OPPORTUNITY",
                    saved.id,
                    before,
                    saved,
                    reasonSafe,
                    "SUCCESS",
                    null
            );
        } catch (Exception ex) {
            System.err.println("AUDIT_CHANGE_STAGE_FAILED: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }

        return saved;
    }

    @Transactional
    public Opportunity closeLost(String id, OpportunityCloseLostRequest req) {
        Opportunity o = repo.findById(id).orElseThrow();
        Opportunity before = cloneOpportunity(o);

        o.stage = "PERDIDO";
        o.updatedAt = Instant.now();

        Opportunity saved = repo.save(o);

        String reasonSafe = (req != null && req.reason() != null && !req.reason().isBlank())
                ? req.reason()
                : "Cierre perdido";

        try {
            auditService.log(
                    "CLOSE_LOST",
                    "OPPORTUNITY",
                    saved.id,
                    before,
                    saved,
                    reasonSafe,
                    "SUCCESS",
                    null
            );
        } catch (Exception ex) {
            System.err.println("AUDIT_CLOSE_LOST_FAILED: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }

        return saved;
    }

    private Opportunity cloneOpportunity(Opportunity o) {
        Opportunity c = new Opportunity();
        c.id = o.id;
        c.customerId = o.customerId;
        c.leadInboxId = o.leadInboxId;
        c.title = o.title;
        c.stage = o.stage;
        c.source = o.source;
        c.ownerUserId = o.ownerUserId;
        c.notes = o.notes;
        c.createdAt = o.createdAt;
        c.updatedAt = o.updatedAt;
        return c;
    }
}