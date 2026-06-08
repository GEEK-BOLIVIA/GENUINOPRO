package com.genuino.crm.quoting.lcl.infra;

import com.genuino.crm.quoting.lcl.domain.TypedProformaLcl;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedProformaLclRepository extends JpaRepository<TypedProformaLcl, UUID> {
}