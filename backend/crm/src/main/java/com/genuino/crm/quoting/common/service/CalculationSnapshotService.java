package com.genuino.crm.quoting.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.genuino.crm.quoting.common.domain.TypedProformaCalculationSnapshot;
import com.genuino.crm.quoting.common.infra.TypedProformaCalculationSnapshotRepository;
import com.genuino.crm.security.SecurityUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.genuino.crm.config.CalculationPolicyService;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;

@Service
public class CalculationSnapshotService {

    private final TypedProformaCalculationSnapshotRepository repository;
    private final TypedProformaRepository typedProformaRepository;
    private final CalculationPolicyService calculationPolicyService;
    private final SecurityUserService securityUserService;
    private final ObjectMapper objectMapper;

    public CalculationSnapshotService(
            TypedProformaCalculationSnapshotRepository repository,
            TypedProformaRepository typedProformaRepository,
            CalculationPolicyService calculationPolicyService,
            SecurityUserService securityUserService,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.typedProformaRepository = typedProformaRepository;
        this.calculationPolicyService = calculationPolicyService;
        this.securityUserService = securityUserService;
        this.objectMapper = objectMapper;
    }

    @Transactional
public TypedProformaCalculationSnapshot createInitialSnapshot(
        UUID proformaId,
        Object input,
        Object output
) {
    TypedProforma proforma =
            typedProformaRepository
                    .findById(proformaId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No existe la proforma "
                                            + proformaId
                                            + " para crear el snapshot."
                            )
                    );

    String modality =
            proforma.getType().name();

    CalculationPolicyService.CalculationPolicyContext context =
            calculationPolicyService.resolveCurrent(
                    modality
            );

    Object parameters =
            CalculationPolicyService.MODE_LIQUIDATION
                    .equals(context.mode())
                    ? context.parameters()
                    : Collections.emptyMap();

    return saveSnapshot(
            proformaId,
            1,
            context.mode(),
            context.policyVersion(),
            input,
            output,
            parameters
    );
}

    @Transactional
    public TypedProformaCalculationSnapshot createNextSnapshot(
            UUID proformaId,
            Object input,
            Object output
    ) {
        Optional<TypedProformaCalculationSnapshot> latest =
                repository
                        .findFirstByProformaIdOrderByCalculationVersionDesc(
                                proformaId
                        );

        if (latest.isEmpty()) {
            return createInitialSnapshot(
                    proformaId,
                    input,
                    output
            );
        }

        TypedProformaCalculationSnapshot previous =
                latest.get();

        int nextVersion =
                previous.getCalculationVersion() + 1;

        return saveSnapshotWithParameterJson(
                proformaId,
                nextVersion,
                previous.getCalculationMode(),
                previous.getCalculationPolicyVersion(),
                input,
                output,
                previous.getParameterSnapshotJson()
        );
    }

private TypedProformaCalculationSnapshot saveSnapshotWithParameterJson(
        UUID proformaId,
        int version,
        String mode,
        String policyVersion,
        Object input,
        Object output,
        String parameterSnapshotJson
) {
    TypedProformaCalculationSnapshot snapshot =
            new TypedProformaCalculationSnapshot();

    snapshot.setId(
            UUID.randomUUID()
    );

    snapshot.setProformaId(
            proformaId
    );

    snapshot.setCalculationVersion(
            version
    );

    snapshot.setCalculationMode(
            normalizeMode(mode)
    );

    snapshot.setCalculationPolicyVersion(
            policyVersion
    );

    snapshot.setInputJson(
            toJson(input)
    );

    snapshot.setOutputJson(
            toJson(output)
    );

    snapshot.setParameterSnapshotJson(
            parameterSnapshotJson == null
                    || parameterSnapshotJson.isBlank()
                    ? "{}"
                    : parameterSnapshotJson
    );

    snapshot.setCreatedAt(
            LocalDateTime.now()
    );

    snapshot.setCreatedBy(
            securityUserService.getCurrentUser()
    );

    return repository.save(snapshot);
}

    @Transactional(readOnly = true)
    public Optional<TypedProformaCalculationSnapshot> findLatest(
            UUID proformaId
    ) {
        return repository
                .findFirstByProformaIdOrderByCalculationVersionDesc(
                        proformaId
                );
    }

    private TypedProformaCalculationSnapshot saveSnapshot(
            UUID proformaId,
            int version,
            String mode,
            String policyVersion,
            Object input,
            Object output,
            Object parameterSnapshot
    ) {
        TypedProformaCalculationSnapshot snapshot =
                new TypedProformaCalculationSnapshot();

        snapshot.setId(
                UUID.randomUUID()
        );

        snapshot.setProformaId(
                proformaId
        );

        snapshot.setCalculationVersion(
                version
        );

        snapshot.setCalculationMode(
                normalizeMode(mode)
        );

        snapshot.setCalculationPolicyVersion(
                policyVersion
        );

        snapshot.setInputJson(
                toJson(input)
        );

        snapshot.setOutputJson(
                toJson(output)
        );

        snapshot.setParameterSnapshotJson(
                toJson(parameterSnapshot)
        );

        snapshot.setCreatedAt(
                LocalDateTime.now()
        );

        snapshot.setCreatedBy(
                securityUserService.getCurrentUser()
        );

        return repository.save(snapshot);
    }

    private String normalizeMode(
            String mode
    ) {
        if (mode == null || mode.isBlank()) {
            throw new IllegalStateException(
                    "La política global de cálculo no tiene un modo configurado."
            );
        }

        String normalized =
                mode.trim().toUpperCase();

        if (!"MODALITY".equals(normalized)
                && !"LIQUIDATION".equals(normalized)) {

            throw new IllegalStateException(
                    "Modo de cálculo no soportado: "
                            + mode
            );
        }

        return normalized;
    }

    private String toJson(
            Object value
    ) {
        try {
            return objectMapper.writeValueAsString(
                    value == null
                            ? Collections.emptyMap()
                            : value
            );

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "No se pudo serializar el snapshot de cálculo.",
                    ex
            );
        }
    }
}