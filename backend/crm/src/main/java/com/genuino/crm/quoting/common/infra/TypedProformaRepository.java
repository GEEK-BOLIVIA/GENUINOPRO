package com.genuino.crm.quoting.common.infra;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.domain.TypedProformaStatus;
import com.genuino.crm.quoting.common.domain.TypedProformaType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedProformaRepository extends JpaRepository<TypedProforma, UUID> {

    List<TypedProforma> findByOpportunityIdOrderByCreatedAtDesc(String opportunityId);

    List<TypedProforma> findByOpportunityIdAndTypeOrderByCreatedAtDesc(
            String opportunityId,
            TypedProformaType type
    );

    List<TypedProforma> findByStatusOrderByCreatedAtDesc(TypedProformaStatus status);

    List<TypedProforma> findByTypeOrderByCreatedAtDesc(TypedProformaType type);
}