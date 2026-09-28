package com.genuino.crm.config;

import com.genuino.crm.config.domain.CalculationParameter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalculationPolicyService {

    public static final String MODE_MODALITY =
            "MODALITY";

    public static final String MODE_LIQUIDATION =
            "LIQUIDATION";

    private final CalculationParameterService parameterService;

    public CalculationPolicyService(
            CalculationParameterService parameterService
    ) {
        this.parameterService = parameterService;
    }

    @Transactional(readOnly = true)
    public CalculationPolicyContext resolveCurrent(
            String modality
    ) {
        String safeModality =
                normalizeModality(modality);

        CalculationParameter policy =
                parameterService.findActive(
                        "GENERAL",
                        "CALCULATION_MODE"
                );

        String mode =
                normalizeMode(
                        policy.getTextValue()
                );

        Map<String, ParameterSnapshotValue> parameters =
                MODE_LIQUIDATION.equals(mode)
                        ? resolveEffectiveParameters(
                                safeModality
                        )
                        : Map.of();

        return new CalculationPolicyContext(
                mode,
                policy.getVersion(),
                safeModality,
                parameters
        );
    }

    @Transactional(readOnly = true)
    public Map<String, ParameterSnapshotValue>
            resolveEffectiveParameters(
                    String modality
            ) {

        String safeModality =
                normalizeModality(modality);

        Map<String, ParameterSnapshotValue> result =
                new LinkedHashMap<>();

        /*
         * Primero cargamos GENERAL.
         * Luego la modalidad sobrescribe únicamente
         * los códigos para los cuales tenga una
         * configuración particular.
         */
        List<CalculationParameter> general =
                parameterService.findByScope(
                        "GENERAL",
                        false
                );

        for (CalculationParameter parameter : general) {

            if ("CALCULATION_MODE".equals(
                    parameter.getCode()
            )) {
                continue;
            }

            result.put(
                    parameter.getCode(),
                    toSnapshotValue(parameter)
            );
        }

        List<CalculationParameter> specific =
                parameterService.findByScope(
                        safeModality,
                        false
                );

        for (CalculationParameter parameter : specific) {

            result.put(
                    parameter.getCode(),
                    toSnapshotValue(parameter)
            );
        }

        return Map.copyOf(result);
    }

    private ParameterSnapshotValue toSnapshotValue(
            CalculationParameter parameter
    ) {
        return new ParameterSnapshotValue(
                parameter.getScope(),
                parameter.getUnit(),
                parameter.getVersion(),
                parameter.getNumericValue(),
                parameter.getTextValue()
        );
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

        if (!MODE_MODALITY.equals(normalized)
                && !MODE_LIQUIDATION.equals(normalized)) {

            throw new IllegalStateException(
                    "Modo de cálculo no soportado: "
                            + mode
            );
        }

        return normalized;
    }

    private String normalizeModality(
            String modality
    ) {
        if (modality == null || modality.isBlank()) {
            throw new IllegalArgumentException(
                    "La modalidad es obligatoria."
            );
        }

        String normalized =
                modality.trim().toUpperCase();

        if (!List.of(
                "LCL",
                "FCL",
                "HBL",
                "AEREO"
        ).contains(normalized)) {

            throw new IllegalArgumentException(
                    "Modalidad de cálculo no válida: "
                            + modality
            );
        }

        return normalized;
    }

    public record CalculationPolicyContext(
            String mode,
            String policyVersion,
            String modality,
            Map<String, ParameterSnapshotValue> parameters
    ) {
    }

    public record ParameterSnapshotValue(
            String sourceScope,
            String unit,
            String version,
            BigDecimal numericValue,
            String textValue
    ) {
    }
}