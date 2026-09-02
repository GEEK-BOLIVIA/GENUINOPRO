package com.genuino.crm.quoting.hbl.infra;

import com.genuino.crm.quoting.hbl.domain.TypedHblProforma;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedHblProformaRepository extends JpaRepository<TypedHblProforma, UUID> {
}