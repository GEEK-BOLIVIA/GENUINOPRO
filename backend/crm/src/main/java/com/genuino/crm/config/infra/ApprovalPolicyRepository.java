package com.genuino.crm.config.infra;

import com.genuino.crm.config.domain.ApprovalPolicy;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalPolicyRepository extends JpaRepository<ApprovalPolicy, UUID> {

    Optional<ApprovalPolicy> findByProformaTypeAndActiveTrue(String proformaType);
}