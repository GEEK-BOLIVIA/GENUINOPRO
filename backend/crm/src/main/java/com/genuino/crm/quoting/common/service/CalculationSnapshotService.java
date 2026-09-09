package com.genuino.crm.quoting.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.genuino.crm.config.CalculationParameterService;
import com.genuino.crm.config.domain.CalculationParameter;
import com.genuino.crm.quoting.common.domain.TypedProformaCalculationSnapshot;
import com.genuino.crm.quoting.common.infra.TypedProformaCalculationSnapshotRepository;
import com.genuino.crm.security.SecurityUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalculationSnapshotService {

    private final TypedProformaCalculationSnapshotRepository repository;
    private final CalculationParameterService parameterService;
    private final SecurityUserService securityUserService;
    private final ObjectMapper objectMapper;

    public CalculationSnapshotService(
            TypedProformaCalculationSnapshotRepository repository,
            CalculationParameterService parameterService,
            SecurityUserService securityUserService,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.parameterService = parameterService;
        this.securityUserService = securityUserService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TypedProformaCalculationSnapshot createInitialSnapshot(
            UUID proformaId,
            Object input,
            Object output
    ) {
        CalculationParameter policy =
                parameterService.findActive(
                        "GENERAL",
                        "CALCULATION_MODE"
                );

        String mode =
                normalizeMode(
                        policy.getTextValue()
                );

        return saveSnapshot(
                proformaId,
                1,
                mode,
                policy.getVersion(),
                input,
                output,
                Collections.emptyMap()
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

        return saveSnapshot(
                proformaId,
                nextVersion,
                previous.getCalculationMode(),
                previous.getCalculationPolicyVersion(),
                input,
                output,
                Collections.emptyMap()
        );
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