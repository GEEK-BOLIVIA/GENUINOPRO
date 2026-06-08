package com.genuino.crm.quoting.common.infra;

import com.genuino.crm.quoting.common.domain.TypedProformaChargeLine;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedProformaChargeLineRepository extends JpaRepository<TypedProformaChargeLine, UUID> {

    List<TypedProformaChargeLine> findByProformaIdOrderBySortOrderAsc(UUID proformaId);

    void deleteByProformaId(UUID proformaId);

    Optional<TypedProformaChargeLine> findByProformaIdAndCode(UUID proformaId, String code);
}