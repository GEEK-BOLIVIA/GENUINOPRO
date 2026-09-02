package com.genuino.crm.quoting.hbl.service;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.domain.TypedProformaStatus;
import com.genuino.crm.quoting.common.domain.TypedProformaType;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;
import com.genuino.crm.quoting.hbl.domain.TypedHblProforma;
import com.genuino.crm.quoting.hbl.dto.CreateTypedHblProformaRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationResponse;
import com.genuino.crm.quoting.hbl.dto.TypedHblProformaDetailResponse;
import com.genuino.crm.quoting.hbl.infra.TypedHblProformaRepository;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TypedHblProformaService {

    private final TypedProformaRepository typedProformaRepository;
    private final TypedHblProformaRepository typedHblProformaRepository;
    private final HblCalculationService calculationService;
    private final HblProformaMapper mapper;

    public TypedHblProformaService(
            TypedProformaRepository typedProformaRepository,
            TypedHblProformaRepository typedHblProformaRepository,
            HblCalculationService calculationService,
            HblProformaMapper mapper
    ) {
        this.typedProformaRepository = typedProformaRepository;
        this.typedHblProformaRepository = typedHblProformaRepository;
        this.calculationService = calculationService;
        this.mapper = mapper;
    }

    @Transactional
    public TypedHblProformaDetailResponse create(CreateTypedHblProformaRequest request) {
        validateOpportunity(request.getOpportunityId());

        UUID proformaId = UUID.randomUUID();
        HblCalculationResponse calculation = calculationService.calculate(request);

        TypedProforma header = new TypedProforma();
        header.setId(proformaId);
        header.setOpportunityId(request.getOpportunityId().trim());
        header.setCustomerId(request.getCustomerId());
        header.setType(TypedProformaType.HBL);
        header.setStatus(TypedProformaStatus.DRAFT);
        header.setCurrency("BOB");
        header.setTotal(calculation.getTotalBob());
        header.setEstimatedProfit(calculation.getGenuinoCommissionBob());
        header.setVersion(1);
        header.setNotes(request.getNotes());
        header.setCreatedBy(resolveActor(request.getCreatedBy()));
        header.setCreatedAt(LocalDateTime.now());

        TypedHblProforma detail = mapper.toEntity(proformaId, request, calculation);
        typedProformaRepository.save(header);
        typedHblProformaRepository.save(detail);

        return mapper.toDetail(header, detail);
    }

    @Transactional
    public TypedHblProformaDetailResponse update(
            UUID id,
            HblCalculationRequest request
    ) {
        TypedProforma header = typedProformaRepository.findById(id)
                .filter(item -> item.getType() == TypedProformaType.HBL)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Proforma HBL no encontrada: " + id
                        )
                );

        if (header.getStatus() != TypedProformaStatus.DRAFT
                && header.getStatus() != TypedProformaStatus.REJECTED) {

            throw new IllegalStateException(
                    "Solo una proforma HBL en borrador o rechazada puede editarse."
            );
        }

        HblCalculationResponse calculation =
                calculationService.calculate(request);

        TypedHblProforma detail =
                mapper.toEntity(
                        id,
                        request,
                        calculation
                );

        typedHblProformaRepository.save(detail);

        header.setTotal(calculation.getTotalBob());
        header.setEstimatedProfit(
                calculation.getGenuinoCommissionBob()
        );
        header.setUpdatedAt(LocalDateTime.now());

        typedProformaRepository.save(header);

        return mapper.toDetail(
                header,
                detail
        );
    }

    @Transactional(readOnly = true)
    public TypedHblProformaDetailResponse getById(UUID id) {
        
        TypedProforma header = typedProformaRepository.findById(id)
                .filter(item -> item.getType() == TypedProformaType.HBL)
                .orElseThrow(() -> new NoSuchElementException("Proforma HBL no encontrada: " + id));

        TypedHblProforma detail = typedHblProformaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Detalle HBL no encontrado: " + id));

        return mapper.toDetail(header, detail);
    }

@Transactional
public TypedHblProformaDetailResponse submitForReview(UUID id) {

    TypedProforma header = getHeader(id);

    if (header.getStatus() != TypedProformaStatus.DRAFT
            && header.getStatus() != TypedProformaStatus.REJECTED) {

        throw new IllegalStateException(
                "Solo una proforma HBL en borrador o rechazada puede enviarse a revisión."
        );
    }

    header.setStatus(TypedProformaStatus.IN_REVIEW);
    header.setUpdatedAt(LocalDateTime.now());

    typedProformaRepository.save(header);

    return getById(id);
}

@Transactional
public TypedHblProformaDetailResponse approve(UUID id) {

    TypedProforma header = getHeader(id);

    if (header.getStatus() != TypedProformaStatus.IN_REVIEW) {
        throw new IllegalStateException(
                "Solo una proforma HBL en revisión puede aprobarse."
        );
    }

    header.setStatus(TypedProformaStatus.APPROVED);
    header.setApprovedAt(LocalDateTime.now());
    header.setUpdatedAt(LocalDateTime.now());

    typedProformaRepository.save(header);

    return getById(id);
}

@Transactional
public TypedHblProformaDetailResponse reject(
        UUID id,
        String reason
) {

    if (reason == null || reason.isBlank()) {
        throw new IllegalArgumentException(
                "El motivo del rechazo es obligatorio."
        );
    }

    TypedProforma header = getHeader(id);

    if (header.getStatus() != TypedProformaStatus.IN_REVIEW) {
        throw new IllegalStateException(
                "Solo una proforma HBL en revisión puede rechazarse."
        );
    }

    header.setStatus(TypedProformaStatus.REJECTED);
    header.setRejectionReason(reason.trim());
    header.setUpdatedAt(LocalDateTime.now());

    typedProformaRepository.save(header);

    return getById(id);
}

@Transactional(readOnly = true)
public List<TypedHblProformaDetailResponse> findAll() {

    return typedProformaRepository
            .findByTypeOrderByCreatedAtDesc(
                    TypedProformaType.HBL
            )
            .stream()
            .map(item -> getById(item.getId()))
            .toList();
}

    private void validateOpportunity(String opportunityId) {
        if (opportunityId == null || opportunityId.isBlank()) {
            throw new IllegalArgumentException("opportunityId es obligatorio");
        }
    }

    private String resolveActor(String actor) {
        return actor == null || actor.isBlank() ? "system" : actor.trim();
    }

    private TypedProforma getHeader(UUID id) {

        return typedProformaRepository.findById(id)
                .filter(item ->
                        item.getType() == TypedProformaType.HBL
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Proforma HBL no encontrada: " + id
                        )
                );
    }
}