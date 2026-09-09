package com.genuino.crm.quoting.common.infra;

import com.genuino.crm.quoting.common.domain.TypedProformaCalculationSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedProformaCalculationSnapshotRepository
        extends JpaRepository<
                TypedProformaCalculationSnapshot,
                UUID
        > {

    Optional<TypedProformaCalculationSnapshot>
            findFirstByProformaIdOrderByCalculationVersionDesc(
                    UUID proformaId
            );

    List<TypedProformaCalculationSnapshot>
            findByProformaIdOrderByCalculationVersionAsc(
                    UUID proformaId
            );
}