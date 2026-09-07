package com.genuino.crm.config.infra;

import com.genuino.crm.config.domain.CalculationParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalculationParameterRepository
        extends JpaRepository<CalculationParameter, UUID> {

    List<CalculationParameter>
    findByScopeAndActiveTrueOrderByCodeAsc(String scope);

    List<CalculationParameter>
    findByScopeOrderByActiveDescCodeAsc(String scope);

    Optional<CalculationParameter>
    findFirstByScopeAndCodeAndActiveTrueOrderByVersionDesc(
            String scope,
            String code
    );
}