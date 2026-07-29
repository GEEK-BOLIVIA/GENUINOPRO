package com.genuino.crm.quoting.fcl;

import com.genuino.crm.config.ProformaRateService;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.domain.TypedProformaStatus;
import com.genuino.crm.quoting.common.domain.TypedProformaType;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;
import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;
import com.genuino.crm.quoting.fcl.infra.TypedFclProformaRepository;
import com.genuino.crm.task.CommercialTaskService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genuino.crm.quoting.common.service.ProformaAccessService;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;

@Service
public class TypedFclProformaService {

    private final TypedFclProformaRepository repository;
    private final ProformaRateService rateService;
    private final TypedProformaRepository typedProformaRepository;
    private final OpportunityRepository opportunityRepository;
    private final CommercialTaskService commercialTaskService;
    private final ProformaAccessService proformaAccessService;
    private final ProformaCustomerSnapshotService customerSnapshotService;

    public TypedFclProformaService(
            TypedFclProformaRepository repository,
            ProformaRateService rateService,
            TypedProformaRepository typedProformaRepository,
            OpportunityRepository opportunityRepository,
            CommercialTaskService commercialTaskService,
            ProformaAccessService proformaAccessService,
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.repository = repository;
        this.rateService = rateService;
        this.typedProformaRepository = typedProformaRepository;
        this.opportunityRepository = opportunityRepository;
        this.commercialTaskService = commercialTaskService;
        this.proformaAccessService = proformaAccessService;
        this.customerSnapshotService = customerSnapshotService;
    }

    @Transactional(readOnly = true)
    public List<TypedFclProforma> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(item -> {
                    try {
                        proformaAccessService.getAuthorizedProforma(item.getId());
                        return true;
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TypedFclProforma findById(UUID id) {
        proformaAccessService.getAuthorizedProforma(id);

        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proforma FCL no encontrada"));
    }

    @Transactional
    public TypedFclProforma create(TypedFclProforma request) {
        UUID proformaId = UUID.randomUUID();

        request.setId(proformaId);
        request.setStatus("DRAFT");
        request.setCurrency("BOB");

        calculateTotals(request);

        String opportunityId = request.getOpportunityId();

        if (opportunityId == null || opportunityId.isBlank()) {
            throw new IllegalArgumentException(
                    "La proforma FCL debe estar asociada a un requerimiento/oportunidad."
            );
        }

        TypedProforma parent = new TypedProforma();
        parent.setId(proformaId);
        parent.setOpportunityId(opportunityId);
        parent.setCustomerId(request.getCustomerId());
        parent.setType(TypedProformaType.FCL);
        parent.setStatus(TypedProformaStatus.DRAFT);
        parent.setCurrency("BOB");
        parent.setTotal(request.getTotalOperationBob() != null ? request.getTotalOperationBob() : request.getTotalBob());
        parent.setEstimatedProfit(request.getGenuinoCommissionBob() != null ? request.getGenuinoCommissionBob() : BigDecimal.ZERO);
        parent.setVersion(1);
        parent.setNotes("Proforma FCL generada desde simulador operativo");
        parent.setCreatedBy("fcl-simulator");
        parent.setCreatedAt(LocalDateTime.now());

        typedProformaRepository.save(parent);

        customerSnapshotService.capture(
                proformaId,
                opportunityId
        );

        TypedFclProforma saved = repository.save(request);

        if (saved.getCode() == null || saved.getCode().isBlank()) {
            saved.setCode("FCL-" + saved.getId().toString().substring(0, 8).toUpperCase());
        }

        TypedFclProforma finalSaved = repository.save(saved);

        opportunityRepository.findById(opportunityId)
                .ifPresent(opportunity -> {
                    opportunity.stage = "PROFORMA_GENERADA";
                    opportunityRepository.save(opportunity);

                    commercialTaskService.createProformaTask(
                            opportunity.id,
                            finalSaved.getId(),
                            "Enviar proforma FCL a revisión",
                            "La proforma FCL fue creada y debe enviarse al flujo de aprobación interna.",
                            "ALTA",
                            opportunity.ownerUserId,
                            OffsetDateTime.now().plusDays(1)
                    );
                });

        return finalSaved;
    }

    @Transactional(readOnly = true)
    public TypedFclProforma calculate(TypedFclProforma request) {
        request.setStatus("DRAFT");
        request.setCurrency("BOB");
        calculateTotals(request);
        return request;
    }

    private void calculateTotals(TypedFclProforma item) {
        BigDecimal exchangeRate = safe(item.getExchangeRate());

        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            exchangeRate = BigDecimal.valueOf(6.96);
            item.setExchangeRate(exchangeRate);
        }

        item.setExchangeRateUsed(exchangeRate);

        BigDecimal taxExchangeRate =
                safe(item.getTaxExchangeRate());

        if (taxExchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            taxExchangeRate = exchangeRate;
            item.setTaxExchangeRate(taxExchangeRate);
        }

        if (
            item.getCalculationRuleVersion() == null
            || item.getCalculationRuleVersion().isBlank()
        ) {
            item.setCalculationRuleVersion(
                    "FCL_GOV_2026_07"
            );
        }

        BigDecimal fobUsd = safe(item.getFobUsd());

        if (fobUsd.compareTo(BigDecimal.ZERO) == 0) {
            fobUsd = safe(item.getMerchandiseValueUsd());
            item.setFobUsd(fobUsd);
        }

        item.setMerchandiseValueUsd(fobUsd);

        BigDecimal maritimeFreightUsd = safe(item.getMaritimeFreightUsd());

        if (maritimeFreightUsd.compareTo(BigDecimal.ZERO) == 0) {
            String containerType = item.getContainerType();

            if (containerType == null || containerType.isBlank()) {
                containerType = "FCL20";
                item.setContainerType(containerType);
            }

            maritimeFreightUsd = rateService.findRatePrice("FCL", containerType, BigDecimal.ZERO);
        }

        item.setMaritimeFreightUsd(maritimeFreightUsd);
        item.setOriginFreightUsd(maritimeFreightUsd);

        BigDecimal insuranceUsd = fobUsd.multiply(new BigDecimal("0.02"));
        item.setInsuranceUsdCalculated(insuranceUsd);
        item.setInsuranceUsd(insuranceUsd);

        BigDecimal containerReleaseUsd =
                safe(item.getContainerReleaseUsd());

        BigDecimal cifBob = fobUsd
                .add(maritimeFreightUsd)
                .add(containerReleaseUsd)
                .add(insuranceUsd)
                .multiply(taxExchangeRate);

        item.setCifBob(cifBob);

        BigDecimal gaPercent = percentOrDefault(item.getGaPercent(), new BigDecimal("10.00"));
        BigDecimal ivaPercent = percentOrDefault(item.getIvaPercent(), new BigDecimal("14.94"));
        BigDecimal icePercent = percentOrDefault(item.getIcePercent(), BigDecimal.ZERO);

        item.setGaPercent(gaPercent);
        item.setIvaPercent(ivaPercent);
        item.setIcePercent(icePercent);

        BigDecimal gaBob = cifBob.multiply(gaPercent).divide(new BigDecimal("100"));
        BigDecimal ivaBob = cifBob.add(gaBob).multiply(ivaPercent).divide(new BigDecimal("100"));
        BigDecimal iceBob = cifBob.add(gaBob).multiply(icePercent).divide(new BigDecimal("100"));

        item.setGaBob(gaBob);
        item.setIvaBob(ivaBob);
        item.setIceBob(iceBob);

        BigDecimal customsTaxesBob = gaBob.add(ivaBob).add(iceBob);
        item.setCustomsTaxesBob(customsTaxesBob);

        BigDecimal alboBob = rateService.findRatePrice("FCL", "ALBO", BigDecimal.ZERO);
        BigDecimal adaBob = rateService.findRatePrice("FCL", "ADA", BigDecimal.ZERO);
        item.setAlboBob(alboBob);
        item.setAdaBob(adaBob);

        BigDecimal genuinoCommissionBob = resolveGenuinoCommission(fobUsd);
        item.setGenuinoCommissionBob(genuinoCommissionBob);

        BigDecimal dispatchAgentCommissionBob = rateService.findRatePrice(
                "FCL",
                "DESPACHANTE",
                BigDecimal.ZERO
        );
        item.setDispatchAgentCommissionBob(dispatchAgentCommissionBob);

        BigDecimal extraNitExpensesBob = BigDecimal.ZERO;

        if ("GENUINO".equalsIgnoreCase(item.getImporterNitType())) {
            extraNitExpensesBob = rateService.findRatePrice(
                    "FCL",
                    "GASTOS_EXTRA_NIT",
                    BigDecimal.ZERO
            );
        }

        item.setExtraNitExpensesBob(extraNitExpensesBob);

        BigDecimal bankTransferCommissionUsd = resolveBankTransferCommission(item, fobUsd);
        item.setBankTransferCommissionUsd(bankTransferCommissionUsd);
        item.setCommissionUsd(bankTransferCommissionUsd);

        item.setTotalUsdToStartOrder(fobUsd.add(bankTransferCommissionUsd));

        BigDecimal subtotalUsd = fobUsd
                .add(bankTransferCommissionUsd)
                .add(maritimeFreightUsd)
                .add(containerReleaseUsd);

        item.setSubtotalUsd(subtotalUsd);
        item.setSubtotalBob(subtotalUsd.multiply(exchangeRate));

        BigDecimal inlandFreightBob = safe(item.getInlandFreightBob());

        BigDecimal miscellaneousExpensesBob =
                safe(item.getMiscellaneousExpensesBob());

        BigDecimal totalOperationBob = inlandFreightBob
                .add(customsTaxesBob)
                .add(alboBob)
                .add(dispatchAgentCommissionBob)
                .add(genuinoCommissionBob)
                .add(extraNitExpensesBob)
                .add(miscellaneousExpensesBob);

                item.setTotalOperationBob(totalOperationBob);
                item.setTotalBob(totalOperationBob);
            }

    private BigDecimal percentOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }

    private BigDecimal resolveGenuinoCommission(BigDecimal fobUsd) {
        return rateService.findRatePrice("FCL", "COMISION_GENUINO", fobUsd);
    }

    private BigDecimal resolveBankTransferCommission(TypedFclProforma item, BigDecimal fobUsd) {
        if (Boolean.TRUE.equals(item.getCustomerPaysSupplier())) {
            return BigDecimal.ZERO;
        }

        String paymentMethod = item.getPaymentMethod();

        if ("ALIBABA".equalsIgnoreCase(paymentMethod)) {
            BigDecimal percent = rateService.findRatePrice(
                    "FCL",
                    "GIRO_ALIBABA_PERCENT",
                    fobUsd
            );

            return fobUsd
                    .multiply(percent.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return rateService.findRatePrice("FCL", "COMISION_GIRO_CHILE", fobUsd);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Transactional
    public TypedFclProforma submitForReview(UUID id) {
        TypedFclProforma item = findById(id);
        item.setStatus("IN_REVIEW");

        typedProformaRepository.findById(id).ifPresent(parent -> {
            parent.setStatus(TypedProformaStatus.IN_REVIEW);
            parent.setUpdatedAt(LocalDateTime.now());
            typedProformaRepository.save(parent);
        });

        TypedFclProforma saved = repository.save(item);

        if (saved.getOpportunityId() != null && !saved.getOpportunityId().isBlank()) {
            opportunityRepository
                    .findById(saved.getOpportunityId())
                    .ifPresent(opportunity -> {
                        opportunity.stage = "APROBACION_INTERNA";
                        opportunityRepository.save(opportunity);
                    });
        }

        return saved;
    }

    @Transactional
    public TypedFclProforma approve(UUID id) {
        TypedFclProforma item = findById(id);
        item.setStatus("APPROVED");

        typedProformaRepository.findById(id).ifPresent(parent -> {
            parent.setStatus(TypedProformaStatus.APPROVED);
            parent.setUpdatedAt(LocalDateTime.now());
            typedProformaRepository.save(parent);
        });

        TypedFclProforma saved = repository.save(item);

        if (saved.getOpportunityId() != null && !saved.getOpportunityId().isBlank()) {
            opportunityRepository
                    .findById(saved.getOpportunityId())
                    .ifPresent(opportunity -> {
                        opportunity.stage = "APROBACION_CLIENTE";
                        opportunityRepository.save(opportunity);
                    });
        }

        return saved;
    }

    @Transactional
    public TypedFclProforma reject(UUID id) {
        TypedFclProforma item = findById(id);
        item.setStatus("REJECTED");

        typedProformaRepository.findById(id).ifPresent(parent -> {
            parent.setStatus(TypedProformaStatus.REJECTED);
            parent.setUpdatedAt(LocalDateTime.now());
            typedProformaRepository.save(parent);
        });

        return repository.save(item);
    }

    @Transactional
    public TypedFclProforma approveByCustomer(UUID id) {
        TypedFclProforma item = findById(id);
        item.setStatus("APPROVED_BY_CUSTOMER");

        typedProformaRepository.findById(id).ifPresent(parent -> {
            parent.setStatus(TypedProformaStatus.CLIENT_ACCEPTED);
            parent.setUpdatedAt(LocalDateTime.now());
            typedProformaRepository.save(parent);
        });

        return repository.save(item);
    }

    @Transactional
    public TypedFclProforma update(UUID id, TypedFclProforma request) {

        TypedFclProforma current = findById(id);

        if (!"DRAFT".equalsIgnoreCase(current.getStatus())) {
            throw new IllegalStateException(
                    "Solo se puede editar una proforma FCL en estado DRAFT"
            );
        }

        request.setId(current.getId());
        request.setStatus(current.getStatus());
        request.setCode(current.getCode());
        
        calculateTotals(request);

        TypedFclProforma saved = repository.save(request);

        typedProformaRepository.findById(id).ifPresent(parent -> {
            parent.setTotal(
                    saved.getTotalOperationBob() != null
                            ? saved.getTotalOperationBob()
                            : saved.getTotalBob()
            );
            parent.setUpdatedAt(LocalDateTime.now());
            typedProformaRepository.save(parent);
        });

        return saved;
    }
}
