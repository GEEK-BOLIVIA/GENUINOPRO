package com.genuino.crm.quoting.lcl.service;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.domain.TypedProformaStatus;
import com.genuino.crm.quoting.common.domain.TypedProformaType;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;
import com.genuino.crm.quoting.lcl.domain.TypedProformaLcl;
import com.genuino.crm.quoting.lcl.dto.CreateTypedLclProformaRequest;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaDetailResponse;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaResponse;
import com.genuino.crm.quoting.lcl.infra.TypedProformaLclRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.genuino.crm.audit.infra.AuditEventRepository;


import com.genuino.crm.quoting.common.infra.TypedProformaChargeLineRepository;

import com.genuino.crm.quoting.common.domain.TypedProformaChargeLine;
import com.genuino.crm.quoting.common.dto.ChargeLineResponse;
import java.util.List;
import java.util.stream.Collectors;

import com.genuino.crm.quoting.common.domain.TypedProformaChargeLine;
import com.genuino.crm.quoting.common.dto.UpdateChargeLineRequest;
import com.genuino.crm.quoting.lcl.dto.RecalculateTypedLclProformaRequest;

import com.genuino.crm.config.infra.ApprovalPolicyRepository;

import com.genuino.crm.audit.domain.AuditEvent;

import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationRequest;
import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationResponse;

import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.infra.OpportunityRepository;

import com.genuino.crm.task.CommercialTaskService;
import java.time.OffsetDateTime;
import com.genuino.crm.quoting.common.service.ProformaAccessService;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;

@Service
public class TypedLclProformaService {

    private final TypedProformaRepository typedProformaRepository;
    private final TypedProformaLclRepository typedProformaLclRepository;
    private final ApprovalPolicyRepository approvalPolicyRepository;
    private final LclCalculationService lclCalculationService;
    private final TypedProformaChargeLineRepository typedProformaChargeLineRepository;
    private final AuditEventRepository auditEventRepository;

    private final LclOperationalCalculationService lclOperationalCalculationService;

    private final OpportunityRepository opportunityRepository;
    private final CommercialTaskService commercialTaskService;

    private final ProformaAccessService proformaAccessService;

    private final ProformaCustomerSnapshotService customerSnapshotService;


    private void validateApprovalRole(BigDecimal total, String actorRole, String type) {

        String role = actorRole == null || actorRole.isBlank()
                ? "VENDEDOR"
                : actorRole.toUpperCase();

        var policy = approvalPolicyRepository
                .findByProformaTypeAndActiveTrue(type)
                .orElseThrow(() -> new IllegalStateException("No existe política de aprobación para " + type));

        if ("ADMIN".equals(role) || "OWNER".equals(role) || "GERENCIA".equals(role)) {
            return;
        }

        if ("SUPERVISOR".equals(role)) {
            if (total.compareTo(policy.getSupervisorLimit()) <= 0) {
                return;
            }
            throw new IllegalStateException("Supervisor excede límite permitido");
        }

        if ("JEFE_COMERCIAL".equals(role)) {
            if (total.compareTo(policy.getCommercialManagerLimit()) <= 0) {
                return;
            }
            throw new IllegalStateException("Jefe comercial excede límite permitido");
        }

        throw new IllegalStateException("Rol no autorizado para aprobar");
    }


    public TypedLclProformaService(
            TypedProformaRepository typedProformaRepository,
            TypedProformaLclRepository typedProformaLclRepository,
            LclCalculationService lclCalculationService,
            TypedProformaChargeLineRepository typedProformaChargeLineRepository,
            AuditEventRepository auditEventRepository,
            ApprovalPolicyRepository approvalPolicyRepository,
            LclOperationalCalculationService lclOperationalCalculationService,
            OpportunityRepository opportunityRepository,
            CommercialTaskService commercialTaskService,
            ProformaAccessService proformaAccessService,
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.typedProformaRepository = typedProformaRepository;
        this.typedProformaLclRepository = typedProformaLclRepository;
        this.lclCalculationService = lclCalculationService;
        this.typedProformaChargeLineRepository = typedProformaChargeLineRepository;
        this.auditEventRepository = auditEventRepository;
        this.approvalPolicyRepository = approvalPolicyRepository;
        this.lclOperationalCalculationService = lclOperationalCalculationService;
        this.opportunityRepository = opportunityRepository;
        this.commercialTaskService = commercialTaskService;
        this.proformaAccessService = proformaAccessService;
        this.customerSnapshotService = customerSnapshotService;
    }

    @Transactional
    public TypedLclProformaResponse create(CreateTypedLclProformaRequest request) {
        UUID proformaId = UUID.randomUUID();

        BigDecimal subtotalCosts = lclCalculationService.calculateSubtotalCosts(request);
        BigDecimal subtotalSell = lclCalculationService.calculateSubtotalSell(request);
        BigDecimal estimatedProfit = lclCalculationService.calculateEstimatedProfit(request);

        TypedProforma proforma = new TypedProforma();
        proforma.setId(proformaId);
        proforma.setOpportunityId(request.getOpportunityId());
        proforma.setCustomerId(request.getCustomerId());
        proforma.setType(TypedProformaType.LCL);
        proforma.setStatus(TypedProformaStatus.DRAFT);
        proforma.setCurrency(request.getCurrency());
        proforma.setTotal(subtotalSell);
        proforma.setEstimatedProfit(estimatedProfit);
        proforma.setVersion(1);
        proforma.setNotes(request.getNotes());
        proforma.setCreatedBy(
                request.getCreatedBy() == null || request.getCreatedBy().isBlank()
                        ? "system"
                        : request.getCreatedBy()
        );
        proforma.setCreatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        customerSnapshotService.capture(
                proformaId,
                request.getOpportunityId()
        );

        if (request.getOpportunityId() != null) {

            opportunityRepository
                    .findById(request.getOpportunityId())
                    .ifPresent(opportunity -> {

                        opportunity.stage = "PROFORMA_GENERADA";

                        opportunityRepository.save(opportunity);

                        commercialTaskService.createProformaTask(
                                opportunity.id,
                                proformaId,
                                "Enviar proforma a revisión",
                                "La proforma fue creada y debe enviarse al flujo de aprobación interna.",
                                "ALTA",
                                opportunity.ownerUserId,
                                OffsetDateTime.now().plusDays(1)
                        );
                    });
        }

        TypedProformaLcl lcl = new TypedProformaLcl();
        lcl.setProformaId(proformaId);

        lcl.setIssueDate(request.getIssueDate());
        lcl.setValidityDays(request.getValidityDays());

        lcl.setSellerName(request.getSellerName());
        lcl.setCustomerName(request.getCustomerName());
        lcl.setCustomerPhone(request.getCustomerPhone());
        lcl.setCustomerAddress(request.getCustomerAddress());

        lcl.setOriginCountry(request.getOriginCountry());
        lcl.setOriginCity(request.getOriginCity());
        lcl.setDestinationCountry(request.getDestinationCountry());
        lcl.setDestinationCity(request.getDestinationCity());
        lcl.setPortOrigin(request.getPortOrigin());
        lcl.setPortDestination(request.getPortDestination());

        lcl.setIncoterm(request.getIncoterm());
        lcl.setCargoType(request.getCargoType());
        lcl.setTransitTime(request.getTransitTime());
        lcl.setCarrierName(request.getCarrierName());
        lcl.setAgentName(request.getAgentName());

        lcl.setPackageCount(request.getPackageCount());
        lcl.setGrossWeightKg(request.getGrossWeightKg());
        lcl.setVolumeCbm(request.getVolumeCbm());
        lcl.setCargoDescription(request.getCargoDescription());

        lcl.setFreightRate(request.getFreightRate());
        lcl.setOriginCharges(request.getOriginCharges());
        lcl.setDestinationCharges(request.getDestinationCharges());
        lcl.setHandlingCharges(request.getHandlingCharges());
        lcl.setDocumentationCharges(request.getDocumentationCharges());
        lcl.setCustomsCharges(request.getCustomsCharges());
        lcl.setInsuranceCharges(request.getInsuranceCharges());
        lcl.setOtherCharges(request.getOtherCharges());
        lcl.setCommissionAmount(request.getCommissionAmount());
        lcl.setMarginAmount(request.getMarginAmount());

        lcl.setSubtotalCosts(subtotalCosts);
        lcl.setSubtotalSell(subtotalSell);
        lcl.setEstimatedProfit(estimatedProfit);

        lcl.setCommercialTerms(request.getCommercialTerms());

        typedProformaLclRepository.save(lcl);

        typedProformaChargeLineRepository.saveAll(
        lclCalculationService.buildChargeLines(proformaId, request));

        TypedLclProformaResponse response = new TypedLclProformaResponse();
        response.setId(proformaId);
        response.setType(TypedProformaType.LCL.name());
        response.setStatus(TypedProformaStatus.DRAFT.name());
        response.setCurrency(request.getCurrency());
        response.setTotal(subtotalSell);
        response.setEstimatedProfit(estimatedProfit);

        return response;
    }

    @Transactional
    public TypedLclProformaDetailResponse createFromOperational(
            LclOperationalCalculationRequest request
    ) {

        LclOperationalCalculationResponse calc =
                lclOperationalCalculationService.calculate(request);

        UUID proformaId = UUID.randomUUID();

        String resolvedOpportunityId = request.getOpportunityId();

        if ((resolvedOpportunityId == null || resolvedOpportunityId.isBlank())
                && request.getCustomerId() != null) {

            resolvedOpportunityId = opportunityRepository
                    .findByLeadInboxId(request.getCustomerId())
                    .map(opportunity -> opportunity.id)
                    .orElse(null);
        }

        TypedProforma proforma = new TypedProforma();
        proforma.setId(proformaId);
        proforma.setType(TypedProformaType.LCL);
        proforma.setOpportunityId(resolvedOpportunityId);
        proforma.setCustomerId(request.getCustomerId());
        proforma.setStatus(TypedProformaStatus.DRAFT);
        proforma.setCurrency("BOB");
        proforma.setTotal(calc.getGrandTotalBs());
        proforma.setEstimatedProfit(calc.getGenuinoCommissionBs());
        proforma.setVersion(1);
        proforma.setNotes("Proforma generada desde simulador operativo");
        proforma.setCreatedBy("operational-simulator");
        proforma.setCreatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        customerSnapshotService.capture(
                proformaId,
                resolvedOpportunityId
        );

        if (resolvedOpportunityId != null) {
            opportunityRepository
                    .findById(resolvedOpportunityId)
                    .ifPresent(opportunity -> {

                        opportunity.stage = "PROFORMA_GENERADA";

                        opportunityRepository.save(opportunity);

                        commercialTaskService.createProformaTask(
                                opportunity.id,
                                proformaId,
                                "Enviar proforma a revisión",
                                "La proforma fue creada y debe enviarse al flujo de aprobación interna.",
                                "ALTA",
                                opportunity.ownerUserId,
                                OffsetDateTime.now().plusDays(1)
                        );
                    });
        }

        TypedProformaLcl lcl = new TypedProformaLcl();

        lcl.setProformaId(proformaId);

        lcl.setCustomerName(request.getCustomerName());
        lcl.setCustomerPhone(request.getCustomerPhone());
        lcl.setCustomerAddress(request.getShippingAddress());

        lcl.setCargoDescription(request.getProductName());

        lcl.setPackageCount(request.getQuantity());

        lcl.setGrossWeightKg(request.getWeightKg());

        lcl.setVolumeCbm(request.getCbm());

        lcl.setExchangeRate(
                calc.getExchangeRate()
        );

        lcl.setTaxExchangeRate(
                calc.getTaxExchangeRate()
        );

        lcl.setCalculationRuleVersion(
                "LCL_GOV_2026_07"
        );

        lcl.setSubtotalSell(calc.getGrandTotalBs());
        lcl.setEstimatedProfit(calc.getGenuinoCommissionBs());

        lcl.setOriginCharges(calc.getFirstPaymentUsd());
        lcl.setDestinationCharges(calc.getSecondPaymentUsd());
        lcl.setCustomsCharges(calc.getCustomsTaxesBs());
        lcl.setOtherCharges(calc.getTotalBs());

        lcl.setSellerName(request.getAdvisorName());
        lcl.setDestinationCity(request.getShippingAddress());
        lcl.setDestinationCountry("Bolivia");
        lcl.setOriginCountry("China");
        lcl.setOriginCity("-");
        lcl.setCargoType("LCL");


        typedProformaLclRepository.save(lcl);

        int sort = 1;

        for (var generatedLine : calc.getGeneratedLines()) {
            TypedProformaChargeLine line = new TypedProformaChargeLine();

            line.setId(UUID.randomUUID());
            line.setProformaId(proformaId);
            line.setLineGroup("SELL");
            line.setCode(generatedLine.getCode());
            line.setDescription(generatedLine.getDescription());
            line.setQuantity(BigDecimal.ONE);
            line.setUnitPrice(generatedLine.getAmount());
            line.setTotal(generatedLine.getAmount());
            line.setEditable(true);
            line.setSortOrder(sort++);

            typedProformaChargeLineRepository.save(line);
        }

        return getById(proformaId);
    }

    @Transactional(readOnly = true)
    public TypedLclProformaDetailResponse getById(UUID id) {
        TypedProforma proforma = proformaAccessService.getAuthorizedProforma(id);

        TypedProformaLcl lcl = typedProformaLclRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el detalle LCL para la proforma " + id));

        TypedLclProformaDetailResponse response = new TypedLclProformaDetailResponse();

        List<TypedProformaChargeLine> lines =
        typedProformaChargeLineRepository.findByProformaIdOrderBySortOrderAsc(id);
        response.setId(proforma.getId());
        response.setOpportunityId(proforma.getOpportunityId());
        response.setCustomerId(proforma.getCustomerId());
        response.setType(proforma.getType().name());
        response.setStatus(proforma.getStatus().name());
        response.setCurrency(proforma.getCurrency());
        response.setTotal(proforma.getTotal());
        response.setEstimatedProfit(proforma.getEstimatedProfit());
        response.setNotes(proforma.getNotes());

        response.setIssueDate(lcl.getIssueDate());
        response.setValidityDays(lcl.getValidityDays());

        response.setSellerName(lcl.getSellerName());
        response.setCustomerName(lcl.getCustomerName());
        response.setCustomerPhone(lcl.getCustomerPhone());
        response.setCustomerAddress(lcl.getCustomerAddress());

        response.setOriginCountry(lcl.getOriginCountry());
        response.setOriginCity(lcl.getOriginCity());
        response.setDestinationCountry(lcl.getDestinationCountry());
        response.setDestinationCity(lcl.getDestinationCity());
        response.setPortOrigin(lcl.getPortOrigin());
        response.setPortDestination(lcl.getPortDestination());

        response.setIncoterm(lcl.getIncoterm());
        response.setCargoType(lcl.getCargoType());
        response.setTransitTime(lcl.getTransitTime());
        response.setCarrierName(lcl.getCarrierName());
        response.setAgentName(lcl.getAgentName());

        response.setPackageCount(lcl.getPackageCount());
        response.setGrossWeightKg(lcl.getGrossWeightKg());
        response.setVolumeCbm(lcl.getVolumeCbm());

        response.setExchangeRate(
                lcl.getExchangeRate()
        );

        response.setTaxExchangeRate(
                lcl.getTaxExchangeRate()
        );

        response.setCalculationRuleVersion(
                lcl.getCalculationRuleVersion()
        );

        response.setCargoDescription(lcl.getCargoDescription());

        response.setFreightRate(lcl.getFreightRate());
        response.setOriginCharges(lcl.getOriginCharges());
        response.setDestinationCharges(lcl.getDestinationCharges());
        response.setHandlingCharges(lcl.getHandlingCharges());
        response.setDocumentationCharges(lcl.getDocumentationCharges());
        response.setCustomsCharges(lcl.getCustomsCharges());
        response.setInsuranceCharges(lcl.getInsuranceCharges());
        response.setOtherCharges(lcl.getOtherCharges());
        response.setCommissionAmount(lcl.getCommissionAmount());
        response.setMarginAmount(lcl.getMarginAmount());

        response.setSubtotalCosts(lcl.getSubtotalCosts());
        response.setSubtotalSell(lcl.getSubtotalSell());
        response.setCommercialTerms(lcl.getCommercialTerms());

    List<ChargeLineResponse> mappedLines = lines.stream().map(line -> {
            ChargeLineResponse dto = new ChargeLineResponse();
            dto.setLineGroup(line.getLineGroup());
            dto.setCode(line.getCode());
            dto.setDescription(line.getDescription());
            dto.setQuantity(line.getQuantity());
            dto.setUnitPrice(line.getUnitPrice());
            dto.setTotal(line.getTotal());
            return dto;
        }).collect(Collectors.toList());

        response.setChargeLines(mappedLines);

        return response;
    }

    @Transactional
    public TypedLclProformaDetailResponse recalculate(UUID id, RecalculateTypedLclProformaRequest request) {
        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma tipificada con id " + id));

        if (!TypedProformaStatus.DRAFT.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo se puede recalcular una proforma en estado DRAFT");
        }

        TypedProformaLcl lcl = typedProformaLclRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el detalle LCL para la proforma " + id));

        if (request.getChargeLines() != null) {
            for (UpdateChargeLineRequest item : request.getChargeLines()) {
                if (item.getCode() == null || item.getCode().isBlank()) {
                    continue;
                }

                TypedProformaChargeLine line = typedProformaChargeLineRepository
                        .findByProformaIdAndCode(id, item.getCode())
                        .orElseThrow(() -> new NoSuchElementException("No existe la línea " + item.getCode()));

                boolean allowedEditableCode = List.of("ALBO", "VAR", "COM").contains(line.getCode());

                if (!allowedEditableCode) {
                    throw new IllegalStateException("La línea " + item.getCode() + " no es editable");
                }

                if (item.getQuantity() != null) {
                    line.setQuantity(item.getQuantity());
                }

                if (item.getUnitPrice() != null) {
                    line.setUnitPrice(item.getUnitPrice());
                }

                line.setTotal(line.getQuantity().multiply(line.getUnitPrice()));
                typedProformaChargeLineRepository.save(line);
            }
        }

        var lines = typedProformaChargeLineRepository.findByProformaIdOrderBySortOrderAsc(id);

        BigDecimal subtotalCosts = lines.stream()
                .filter(line -> "COST".equals(line.getLineGroup()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal usdSubtotal = lines.stream()
                .filter(line -> List.of("FOB", "GIRO", "MAR").contains(line.getCode()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bsSubtotal = lines.stream()
                .filter(line -> List.of("ADU", "ALBO", "VAR", "COM").contains(line.getCode()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal exchangeRate = BigDecimal.TEN;

        BigDecimal total = usdSubtotal
                .multiply(exchangeRate)
                .add(bsSubtotal)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal estimatedProfit = lines.stream()
                .filter(line -> !"COST".equals(line.getLineGroup()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        proforma.setTotal(total);
        proforma.setEstimatedProfit(estimatedProfit);
        proforma.setUpdatedBy(
                request.getUpdatedBy() == null || request.getUpdatedBy().isBlank()
                        ? "system"
                        : request.getUpdatedBy()
        );
        proforma.setUpdatedAt(LocalDateTime.now());
        typedProformaRepository.save(proforma);

        lcl.setSubtotalCosts(subtotalCosts);
        lcl.setSubtotalSell(total);
        lcl.setEstimatedProfit(estimatedProfit);

        for (TypedProformaChargeLine line : lines) {
            switch (line.getCode()) {
                case "ORG" -> lcl.setOriginCharges(line.getUnitPrice());
                case "DST" -> lcl.setDestinationCharges(line.getUnitPrice());
                case "HDL" -> lcl.setHandlingCharges(line.getUnitPrice());
                case "DOC" -> lcl.setDocumentationCharges(line.getUnitPrice());
                case "CUS" -> lcl.setCustomsCharges(line.getUnitPrice());
                case "INS" -> lcl.setInsuranceCharges(line.getUnitPrice());
                case "OTH" -> lcl.setOtherCharges(line.getUnitPrice());
                case "COM" -> lcl.setCommissionAmount(line.getUnitPrice());
                case "MRG" -> lcl.setMarginAmount(line.getUnitPrice());
                default -> {
                    // FRT no se actualiza porque no es editable por ahora
                }
            }
        }

        typedProformaLclRepository.save(lcl);

        return getById(id);
    }

    @Transactional
    public TypedLclProformaDetailResponse submitForReview(UUID id, String updatedBy) {
        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma tipificada con id " + id));

        if (!TypedProformaStatus.DRAFT.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo una proforma en DRAFT puede enviarse a revisión");
        }

        String safeActor = updatedBy == null || updatedBy.isBlank() ? "system" : updatedBy;

        proforma.setStatus(TypedProformaStatus.IN_REVIEW);
        proforma.setUpdatedBy(safeActor);
        proforma.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        if (proforma.getOpportunityId() != null) {
            opportunityRepository.findById(proforma.getOpportunityId())
                    .ifPresent(opportunity -> {
                        opportunity.stage = "APROBACION_INTERNA";
                        opportunityRepository.save(opportunity);
                    });
        }

        AuditEvent audit = new AuditEvent();
        audit.auditId = UUID.randomUUID();
        audit.ts = Instant.now();
        audit.entityType = "PROFORMA";
        audit.entityId = proforma.getId().toString();
        audit.action = "SUBMITTED_FOR_APPROVAL";
        audit.actorUserId = safeActor;
        audit.reason = null;
        audit.result = "OK";

        auditEventRepository.save(audit);

        return getById(id);
    }

    @Transactional
    public TypedLclProformaDetailResponse approve(UUID id, String actor, String actorRole) {

        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma tipificada con id " + id));

        if (!TypedProformaStatus.IN_REVIEW.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo una proforma pendiente de aprobación puede aprobarse");
        }

        // 🔥 VALIDACIÓN DE ROLES
        validateApprovalRole(
                proforma.getTotal(),
                actorRole,
                proforma.getType().name()
        );

        String safeActor = actor == null || actor.isBlank() ? "system" : actor;

        proforma.setStatus(TypedProformaStatus.APPROVED);
        proforma.setApprovedBy(safeActor);
        proforma.setApprovedAt(LocalDateTime.now());
        proforma.setUpdatedBy(safeActor);
        proforma.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        if (proforma.getOpportunityId() != null) {
            opportunityRepository.findById(proforma.getOpportunityId())
                    .ifPresent(opportunity -> {
                        opportunity.stage = "APROBACION_CLIENTE";
                        opportunityRepository.save(opportunity);
                    });
        }

        AuditEvent audit = new AuditEvent();
        audit.auditId = UUID.randomUUID();
        audit.ts = Instant.now();
        audit.entityType = "PROFORMA";
        audit.entityId = proforma.getId().toString();
        audit.action = "APPROVED";
        audit.actorUserId = safeActor;
        audit.reason = null;
        audit.result = "OK";

        auditEventRepository.save(audit);

        return getById(id);
    }

    @Transactional
    public TypedLclProformaDetailResponse reject(UUID id, String actor, String reason) {
        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma tipificada con id " + id));

        if (!TypedProformaStatus.IN_REVIEW.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo una proforma pendiente de aprobación puede rechazarse");
        }

        String safeActor = actor == null || actor.isBlank() ? "system" : actor;

        proforma.setStatus(TypedProformaStatus.REJECTED);
        proforma.setRejectionReason(reason);
        proforma.setUpdatedBy(safeActor);
        proforma.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        AuditEvent audit = new AuditEvent();
        audit.auditId = UUID.randomUUID();
        audit.ts = Instant.now();
        audit.entityType = "PROFORMA";
        audit.entityId = proforma.getId().toString();
        audit.action = "REJECTED";
        audit.actorUserId = safeActor;
        audit.reason = reason;
        audit.result = "OK";

        auditEventRepository.save(audit);

        return getById(id);
    }
    @Transactional(readOnly = true)
    public List<TypedLclProformaResponse> findAll() {
            return typedProformaRepository
                    .findByTypeOrderByCreatedAtDesc(TypedProformaType.LCL)
                    .stream()
                    .filter(proforma -> {
                        try {
                            proformaAccessService.getAuthorizedProforma(proforma.getId());
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
                    })
                .map(proforma -> {
                    TypedProformaLcl lcl = typedProformaLclRepository
                            .findById(proforma.getId())
                            .orElse(null);

                    TypedLclProformaResponse response = new TypedLclProformaResponse();

                    response.setId(proforma.getId());
                    response.setCustomerId(proforma.getCustomerId());
                    response.setType(proforma.getType().name());
                    response.setStatus(proforma.getStatus().name());
                    response.setCurrency(proforma.getCurrency());
                    response.setTotal(calculateGrandTotalFromLines(proforma.getId()));
                    response.setEstimatedProfit(proforma.getEstimatedProfit());
                    response.setCreatedAt(proforma.getCreatedAt());

                    if (lcl != null) {
                        response.setCustomerName(lcl.getCustomerName());
                        response.setOriginCity(lcl.getOriginCity());
                        response.setDestinationCity(lcl.getDestinationCity());
                        response.setPortOrigin(lcl.getPortOrigin());
                        response.setPortDestination(lcl.getPortDestination());
                        response.setSellerName(lcl.getSellerName());
                        response.setIssueDate(lcl.getIssueDate());
                        response.setCargoDescription(lcl.getCargoDescription());
                        response.setOpportunityId(proforma.getOpportunityId());
                    }

                    return response;
                })
                .toList();
    }

    private BigDecimal calculateGrandTotalFromLines(UUID proformaId) {
        var lines = typedProformaChargeLineRepository.findByProformaIdOrderBySortOrderAsc(proformaId);

        BigDecimal usdSubtotal = lines.stream()
                .filter(line -> List.of("FOB", "GIRO", "MAR").contains(line.getCode()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bsSubtotal = lines.stream()
                .filter(line -> List.of("ADU", "ALBO", "VAR", "COM").contains(line.getCode()))
                .map(TypedProformaChargeLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return usdSubtotal
                .multiply(BigDecimal.TEN)
                .add(bsSubtotal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public TypedLclProformaDetailResponse clientAccept(UUID id, String actor) {
        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma " + id));

        if (!TypedProformaStatus.APPROVED.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo una proforma aprobada internamente puede ser aprobada por el cliente");
        }

        String safeActor = actor == null || actor.isBlank() ? "advisor" : actor;

        proforma.setStatus(TypedProformaStatus.CLIENT_ACCEPTED);
        proforma.setUpdatedBy(safeActor);
        proforma.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        if (proforma.getOpportunityId() != null) {
            opportunityRepository.findById(proforma.getOpportunityId())
                    .ifPresent(opportunity -> {
                        opportunity.stage = "CLIENTE";
                        opportunityRepository.save(opportunity);
                    });
        }

        AuditEvent audit = new AuditEvent();
        audit.auditId = UUID.randomUUID();
        audit.ts = Instant.now();
        audit.entityType = "PROFORMA";
        audit.entityId = proforma.getId().toString();
        audit.action = "CLIENT_ACCEPTED";
        audit.actorUserId = safeActor;
        audit.reason = "Cliente aceptó la proforma";
        audit.result = "OK";

        auditEventRepository.save(audit);

        return getById(id);
    }

    @Transactional
    public TypedLclProformaDetailResponse clientReject(UUID id, String actor, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo del cliente es obligatorio");
        }

        TypedProforma proforma = typedProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la proforma " + id));

        if (!TypedProformaStatus.APPROVED.equals(proforma.getStatus())) {
            throw new IllegalStateException("Solo una proforma aprobada internamente puede ser rechazada por el cliente");
        }

        String safeActor = actor == null || actor.isBlank() ? "advisor" : actor;

        proforma.setStatus(TypedProformaStatus.CLIENT_REJECTED);
        proforma.setRejectionReason(reason);
        proforma.setUpdatedBy(safeActor);
        proforma.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(proforma);

        AuditEvent audit = new AuditEvent();
        audit.auditId = UUID.randomUUID();
        audit.ts = Instant.now();
        audit.entityType = "PROFORMA";
        audit.entityId = proforma.getId().toString();
        audit.action = "CLIENT_REJECTED";
        audit.actorUserId = safeActor;
        audit.reason = reason;
        audit.result = "OK";

        auditEventRepository.save(audit);

        return getById(id);
    }


}