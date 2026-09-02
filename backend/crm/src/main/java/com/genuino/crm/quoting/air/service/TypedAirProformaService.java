package com.genuino.crm.quoting.air.service;

import com.genuino.crm.quoting.air.domain.TypedAirProforma;
import com.genuino.crm.quoting.air.dto.AirCalculationRequest;
import com.genuino.crm.quoting.air.dto.AirCalculationResponse;
import com.genuino.crm.quoting.air.dto.CreateTypedAirProformaRequest;
import com.genuino.crm.quoting.air.dto.TypedAirProformaDetailResponse;
import com.genuino.crm.quoting.air.infra.TypedAirProformaRepository;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.domain.TypedProformaStatus;
import com.genuino.crm.quoting.common.domain.TypedProformaType;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypedAirProformaService {

    private final TypedProformaRepository typedProformaRepository;
    private final TypedAirProformaRepository typedAirProformaRepository;
    private final AirCalculationService calculationService;
    private final AirProformaMapper mapper;

    public TypedAirProformaService(
            TypedProformaRepository typedProformaRepository,
            TypedAirProformaRepository typedAirProformaRepository,
            AirCalculationService calculationService,
            AirProformaMapper mapper
    ) {
        this.typedProformaRepository = typedProformaRepository;
        this.typedAirProformaRepository = typedAirProformaRepository;
        this.calculationService = calculationService;
        this.mapper = mapper;
    }

    @Transactional
    public TypedAirProformaDetailResponse create(
            CreateTypedAirProformaRequest request
    ) {

        validateOpportunity(
                request.getOpportunityId()
        );

        UUID proformaId =
                UUID.randomUUID();

        AirCalculationResponse calculation =
                calculationService.calculate(
                        request
                );

        TypedProforma header =
                new TypedProforma();

        header.setId(
                proformaId
        );

        header.setOpportunityId(
                request.getOpportunityId().trim()
        );

        header.setCustomerId(
                request.getCustomerId()
        );

        header.setType(
                TypedProformaType.AEREO
        );

        header.setStatus(
                TypedProformaStatus.DRAFT
        );

        header.setCurrency(
                "BOB"
        );

        header.setTotal(
                calculation.getTotalBob()
        );

        header.setEstimatedProfit(
                calculation.getGenuinoCommissionBob()
        );

        header.setVersion(
                1
        );

        header.setNotes(
                request.getNotes()
        );

        header.setCreatedBy(
                resolveActor(
                        request.getCreatedBy()
                )
        );

        header.setCreatedAt(
                LocalDateTime.now()
        );

        TypedAirProforma detail =
                mapper.toEntity(
                        proformaId,
                        request,
                        calculation
                );

        typedProformaRepository.save(
                header
        );

        typedAirProformaRepository.save(
                detail
        );

        return mapper.toDetail(
                header,
                detail
        );
    }

    @Transactional
    public TypedAirProformaDetailResponse update(
            UUID id,
            AirCalculationRequest request
    ) {

        TypedProforma header =
                getHeader(id);

        if (header.getStatus()
                != TypedProformaStatus.DRAFT
                && header.getStatus()
                != TypedProformaStatus.REJECTED) {

            throw new IllegalStateException(
                    "Solo una proforma Aéreo en borrador o rechazada puede editarse."
            );
        }

        AirCalculationResponse calculation =
                calculationService.calculate(
                        request
                );

        TypedAirProforma detail =
                mapper.toEntity(
                        id,
                        request,
                        calculation
                );

        typedAirProformaRepository.save(
                detail
        );

        header.setTotal(
                calculation.getTotalBob()
        );

        header.setEstimatedProfit(
                calculation.getGenuinoCommissionBob()
        );

        header.setUpdatedAt(
                LocalDateTime.now()
        );

        typedProformaRepository.save(
                header
        );

        return mapper.toDetail(
                header,
                detail
        );
    }

    @Transactional(readOnly = true)
    public TypedAirProformaDetailResponse getById(
            UUID id
    ) {

        TypedProforma header =
                getHeader(id);

        TypedAirProforma detail =
                typedAirProformaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Detalle Aéreo no encontrado: "
                                                + id
                                )
                        );

        return mapper.toDetail(
                header,
                detail
        );
    }

    @Transactional(readOnly = true)
    public List<TypedAirProformaDetailResponse> findAll() {

        return typedProformaRepository
                .findByTypeOrderByCreatedAtDesc(
                        TypedProformaType.AEREO
                )
                .stream()
                .map(item ->
                        getById(
                                item.getId()
                        )
                )
                .toList();
    }

    @Transactional
    public TypedAirProformaDetailResponse submitForReview(
            UUID id
    ) {

        TypedProforma header =
                getHeader(id);

        if (header.getStatus()
                != TypedProformaStatus.DRAFT
                && header.getStatus()
                != TypedProformaStatus.REJECTED) {

            throw new IllegalStateException(
                    "Solo una proforma Aéreo en borrador o rechazada puede enviarse a revisión."
            );
        }

        header.setStatus(
                TypedProformaStatus.IN_REVIEW
        );

        header.setUpdatedAt(
                LocalDateTime.now()
        );

        typedProformaRepository.save(
                header
        );

        return getById(id);
    }

    @Transactional
    public TypedAirProformaDetailResponse approve(
            UUID id
    ) {

        TypedProforma header =
                getHeader(id);

        if (header.getStatus()
                != TypedProformaStatus.IN_REVIEW) {

            throw new IllegalStateException(
                    "Solo una proforma Aéreo en revisión puede aprobarse."
            );
        }

        header.setStatus(
                TypedProformaStatus.APPROVED
        );

        header.setApprovedAt(
                LocalDateTime.now()
        );

        header.setUpdatedAt(
                LocalDateTime.now()
        );

        typedProformaRepository.save(
                header
        );

        return getById(id);
    }

    @Transactional
    public TypedAirProformaDetailResponse reject(
            UUID id,
            String reason
    ) {

        if (reason == null
                || reason.isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo del rechazo es obligatorio."
            );
        }

        TypedProforma header =
                getHeader(id);

        if (header.getStatus()
                != TypedProformaStatus.IN_REVIEW) {

            throw new IllegalStateException(
                    "Solo una proforma Aéreo en revisión puede rechazarse."
            );
        }

        header.setStatus(
                TypedProformaStatus.REJECTED
        );

        header.setRejectionReason(
                reason.trim()
        );

        header.setUpdatedAt(
                LocalDateTime.now()
        );

        typedProformaRepository.save(
                header
        );

        return getById(id);
    }

    private TypedProforma getHeader(
            UUID id
    ) {

        return typedProformaRepository
                .findById(id)
                .filter(item ->
                        item.getType()
                                == TypedProformaType.AEREO
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Proforma Aéreo no encontrada: "
                                        + id
                        )
                );
    }

    private void validateOpportunity(
            String opportunityId
    ) {

        if (opportunityId == null
                || opportunityId.isBlank()) {

            throw new IllegalArgumentException(
                    "opportunityId es obligatorio"
            );
        }
    }

    private String resolveActor(
            String actor
    ) {

        return actor == null
                || actor.isBlank()
                ? "system"
                : actor.trim();
    }
}