package com.genuino.crm.config.infra;

import com.genuino.crm.config.domain.ProformaRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProformaRateRepository extends JpaRepository<ProformaRate, UUID> {

    List<ProformaRate> findByProformaTypeAndActiveTrueOrderByRateTypeAscRangeFromAsc(
            String proformaType
    );

    List<ProformaRate> findByProformaTypeOrderByActiveDescRateTypeAscRangeFromAsc(
            String proformaType
    );

    Optional<ProformaRate> findFirstByProformaTypeAndRateTypeAndActiveTrueAndRangeFromLessThanEqualAndRangeToGreaterThanEqual(
            String proformaType,
            String rateType,
            BigDecimal valueFrom,
            BigDecimal valueTo
    );

    Optional<ProformaRate> findFirstByProformaTypeAndRateTypeAndActiveTrueAndRangeFromLessThanEqualAndRangeToIsNull(
            String proformaType,
            String rateType,
            BigDecimal value
    );
}