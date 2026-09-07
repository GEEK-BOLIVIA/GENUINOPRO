package com.genuino.crm.config;

import com.genuino.crm.config.domain.CalculationParameter;
import com.genuino.crm.config.infra.CalculationParameterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CalculationParameterService {

    private final CalculationParameterRepository repository;

    public CalculationParameterService(
            CalculationParameterRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CalculationParameter> findByScope(
            String scope,
            boolean includeInactive
    ) {
        String safeScope = normalizeScope(scope);

        if (includeInactive) {
            return repository.findByScopeOrderByActiveDescCodeAsc(safeScope);
        }

        return repository.findByScopeAndActiveTrueOrderByCodeAsc(safeScope);
    }

    @Transactional(readOnly = true)
    public CalculationParameter findActive(
            String scope,
            String code
    ) {
        return repository
                .findFirstByScopeAndCodeAndActiveTrueOrderByVersionDesc(
                        normalizeScope(scope),
                        normalizeCode(code)
                )
                .orElseThrow(() -> new IllegalStateException(
                        "No existe parámetro activo para "
                                + scope + " / " + code
                ));
    }

    @Transactional(readOnly = true)
    public BigDecimal findNumericValue(
            String scope,
            String code
    ) {
        CalculationParameter parameter =
                findActive(scope, code);

        if (parameter.getNumericValue() == null) {
            throw new IllegalStateException(
                    "El parámetro " + scope + " / " + code
                            + " no tiene valor numérico."
            );
        }

        return parameter.getNumericValue();
    }

    @Transactional
    public CalculationParameter create(
            CalculationParameter request
    ) {
        request.setId(null);

        if (request.getActive() == null) {
            request.setActive(true);
        }

        validate(request);

        return repository.save(request);
    }

    @Transactional
    public CalculationParameter update(
            UUID id,
            CalculationParameter request
    ) {
        CalculationParameter current =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Parámetro no encontrado"
                                )
                        );

        current.setScope(request.getScope());
        current.setCode(request.getCode());
        current.setNumericValue(request.getNumericValue());
        current.setTextValue(request.getTextValue());
        current.setUnit(request.getUnit());
        current.setVersion(request.getVersion());
        current.setActive(request.getActive());
        current.setDescription(request.getDescription());

        validate(current);

        return repository.save(current);
    }

    @Transactional
    public void deactivate(UUID id) {
        CalculationParameter current =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Parámetro no encontrado"
                                )
                        );

        current.setActive(false);
        repository.save(current);
    }

    @Transactional
    public CalculationParameter activate(UUID id) {
        CalculationParameter current =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Parámetro no encontrado"
                                )
                        );

        current.setActive(true);

        return repository.save(current);
    }

    private void validate(
            CalculationParameter parameter
    ) {
        parameter.setScope(
                normalizeScope(parameter.getScope())
        );

        parameter.setCode(
                normalizeCode(parameter.getCode())
        );

        if (parameter.getUnit() == null
                || parameter.getUnit().isBlank()) {
            throw new IllegalArgumentException(
                    "La unidad del parámetro es obligatoria."
            );
        }

        String unit =
                parameter.getUnit()
                        .trim()
                        .toUpperCase();

        if (!List.of(
                "PERCENT",
                "RATE",
                "USD",
                "BOB",
                "BOOLEAN",
                "TEXT"
        ).contains(unit)) {
            throw new IllegalArgumentException(
                    "Unidad de parámetro inválida."
            );
        }

        parameter.setUnit(unit);

        if ("PERCENT".equals(unit)
                && parameter.getNumericValue() != null
                && (
                    parameter.getNumericValue()
                            .compareTo(BigDecimal.ZERO) < 0
                    ||
                    parameter.getNumericValue()
                            .compareTo(BigDecimal.ONE) > 0
                )) {
            throw new IllegalArgumentException(
                    "Los porcentajes deben almacenarse entre 0 y 1."
            );
        }

        if (parameter.getVersion() == null
                || parameter.getVersion().isBlank()) {
            parameter.setVersion("LIQ_2026_09");
        }
    }

    private String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) {
            throw new IllegalArgumentException(
                    "El alcance del parámetro es obligatorio."
            );
        }

        String normalized =
                scope.trim().toUpperCase();

        if (!List.of(
                "GENERAL",
                "LCL",
                "FCL",
                "HBL",
                "AEREO"
        ).contains(normalized)) {
            throw new IllegalArgumentException(
                    "Alcance de parámetro inválido."
            );
        }

        return normalized;
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del parámetro es obligatorio."
            );
        }

        return code.trim().toUpperCase();
    }
}