package com.genuino.crm.quoting.fcl.infra;

import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TypedFclProformaRepository extends JpaRepository<TypedFclProforma, UUID> {

    List<TypedFclProforma> findAllByOrderByCreatedAtDesc();
}