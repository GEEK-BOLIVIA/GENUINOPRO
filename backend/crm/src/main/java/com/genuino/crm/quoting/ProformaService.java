package com.genuino.crm.quoting;

import com.genuino.crm.audit.AuditService;
import com.genuino.crm.opportunity.OpportunityConversionService;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.domain.Proforma;
import com.genuino.crm.quoting.domain.ProformaSequence;
import com.genuino.crm.quoting.dto.ProformaCreateRequest;
import com.genuino.crm.quoting.infra.ProformaRepository;
import com.genuino.crm.quoting.infra.ProformaSequenceRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ProformaService {

    private final ProformaRepository proformaRepository;
    private final ProformaSequenceRepository proformaSequenceRepository;
    private final AuditService auditService;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityConversionService opportunityConversionService;

    public ProformaService(
            ProformaRepository proformaRepository,
            ProformaSequenceRepository proformaSequenceRepository,
            AuditService auditService,
            OpportunityRepository opportunityRepository,
            OpportunityConversionService opportunityConversionService
    ) {
        this.proformaRepository = proformaRepository;
        this.proformaSequenceRepository = proformaSequenceRepository;
        this.auditService = auditService;
        this.opportunityRepository = opportunityRepository;
        this.opportunityConversionService = opportunityConversionService;
    }

    @Transactional
    public Proforma create(ProformaCreateRequest req) {
        String actor = SecurityContextHolder.getContext().getAuthentication().getName();

        Proforma p = new Proforma();
        p.id = "quo_" + UUID.randomUUID();
        p.customerId = req.customerId();
        p.opportunityId = req.opportunityId();
        p.currency = req.currency();
        p.status = "DRAFT";

        p.subtotal = req.amount();
        p.discount = BigDecimal.ZERO;
        p.total = req.amount();

        p.series = "A";
        p.year = Instant.now().atZone(ZoneOffset.UTC).getYear();
        p.number = null;

        p.createdBy = actor;
        p.version = 0L;
        p.createdAt = Instant.now();
        p.updatedAt = Instant.now();

        Proforma saved = proformaRepository.save(p);
        auditService.log("CREATE", "PROFORMA", saved.id, null, saved, null, "SUCCESS", null);

        if (saved.opportunityId != null && !saved.opportunityId.isBlank()) {
            opportunityRepository.findById(saved.opportunityId).ifPresent(opportunity -> {
                if (!"WON".equals(opportunity.stage) && !"LOST".equals(opportunity.stage)) {
                    opportunity.stage = "PROPOSAL";
                    opportunity.updatedAt = Instant.now();
                    opportunityRepository.save(opportunity);
                }
            });
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public Proforma getById(String id) {
        return proformaRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Proforma submitReview(String id) {
        Proforma p = proformaRepository.findById(id).orElseThrow();

        if (!"DRAFT".equals(p.status)) {
            throw new IllegalStateException("Only DRAFT can be submitted");
        }

        Proforma before = cloneProforma(p);

        p.status = "IN_REVIEW";
        p.submittedAt = Instant.now();
        p.updatedAt = Instant.now();

        Proforma saved = proformaRepository.save(p);
        auditService.log("SUBMIT_REVIEW", "PROFORMA", saved.id, before, saved, null, "SUCCESS", null);
        return saved;
    }

    @Transactional
    public Proforma approve(String id, String reason) {
        String actor = SecurityContextHolder.getContext().getAuthentication().getName();

        Proforma p = proformaRepository.findById(id).orElseThrow();

        if (!"IN_REVIEW".equals(p.status)) {
            throw new IllegalStateException("Only IN_REVIEW can be approved");
        }

        if (actor.equals(p.createdBy)) {
            throw new SecurityException("Creator cannot approve own proforma");
        }

        Proforma before = cloneProforma(p);

        ProformaSequence seq = proformaSequenceRepository
                .findById(new ProformaSequence.Pk(p.year, p.series))
                .orElseGet(() -> {
                    ProformaSequence s = new ProformaSequence();
                    s.year = p.year;
                    s.series = p.series;
                    s.lastValue = 0;
                    s.version = 0L;
                    return proformaSequenceRepository.save(s);
                });

        int next = seq.lastValue + 1;
        seq.lastValue = next;
        proformaSequenceRepository.save(seq);

        p.number = next;
        p.status = "APPROVED";
        p.approvedAt = Instant.now();
        p.updatedAt = Instant.now();

        Proforma saved = proformaRepository.save(p);
        auditService.log("APPROVE", "PROFORMA", saved.id, before, saved, reason, "SUCCESS", null);

        if (saved.opportunityId != null && !saved.opportunityId.isBlank()) {
            opportunityRepository.findById(saved.opportunityId).ifPresent(opportunity -> {
                if (!"LOST".equals(opportunity.stage)) {
                    opportunity.stage = "WON";
                    opportunity.updatedAt = Instant.now();
                    opportunityRepository.save(opportunity);

                    if (opportunity.customerId == null || opportunity.customerId.isBlank()) {
                        opportunityConversionService.ensureCustomerForWonOpportunity(opportunity);
                    }
                }
            });
        }

        return saved;
    }

    @Transactional
    public Proforma reject(String id, String reason) {
        Proforma p = proformaRepository.findById(id).orElseThrow();

        if (!"IN_REVIEW".equals(p.status)) {
            throw new IllegalStateException("Only IN_REVIEW can be rejected");
        }

        Proforma before = cloneProforma(p);

        p.status = "REJECTED";
        p.rejectedAt = Instant.now();
        p.updatedAt = Instant.now();

        Proforma saved = proformaRepository.save(p);
        auditService.log("REJECT", "PROFORMA", saved.id, before, saved, reason, "SUCCESS", null);
        return saved;
    }

    private Proforma cloneProforma(Proforma p) {
        Proforma c = new Proforma();
        c.id = p.id;
        c.customerId = p.customerId;
        c.opportunityId = p.opportunityId;
        c.status = p.status;
        c.currency = p.currency;
        c.subtotal = p.subtotal;
        c.discount = p.discount;
        c.total = p.total;
        c.series = p.series;
        c.year = p.year;
        c.number = p.number;
        c.createdBy = p.createdBy;
        c.pdfS3Key = p.pdfS3Key;
        c.version = p.version;
        c.submittedAt = p.submittedAt;
        c.approvedAt = p.approvedAt;
        c.rejectedAt = p.rejectedAt;
        c.createdAt = p.createdAt;
        c.updatedAt = p.updatedAt;
        return c;
    }
}