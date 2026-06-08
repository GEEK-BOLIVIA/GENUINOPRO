package com.genuino.crm.config;

import com.genuino.crm.config.domain.ApprovalPolicy;
import com.genuino.crm.config.infra.ApprovalPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genuino.crm.config.dto.UpdateApprovalPolicyRequest;
import java.time.LocalDateTime;

@Service
public class ApprovalPolicyService {

    private final ApprovalPolicyRepository approvalPolicyRepository;

    public ApprovalPolicyService(ApprovalPolicyRepository approvalPolicyRepository) {
        this.approvalPolicyRepository = approvalPolicyRepository;
    }

    @Transactional(readOnly = true)
    public ApprovalPolicy getActivePolicy(String proformaType) {
        return approvalPolicyRepository.findByProformaTypeAndActiveTrue(proformaType)
                .orElseThrow(() -> new IllegalStateException("No existe política activa para " + proformaType));
    }

    @Transactional
    public ApprovalPolicy updatePolicy(String proformaType, UpdateApprovalPolicyRequest request) {
        ApprovalPolicy policy = getActivePolicy(proformaType);

        if (request.getSupervisorLimit().compareTo(request.getCommercialManagerLimit()) > 0) {
            throw new IllegalStateException("El límite del supervisor no puede superar al límite del jefe comercial");
        }

        policy.setSupervisorLimit(request.getSupervisorLimit());
        policy.setCommercialManagerLimit(request.getCommercialManagerLimit());
        policy.setUpdatedAt(LocalDateTime.now());

        return approvalPolicyRepository.save(policy);
    }
}