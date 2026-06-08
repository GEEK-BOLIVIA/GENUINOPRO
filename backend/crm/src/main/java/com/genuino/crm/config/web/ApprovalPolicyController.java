package com.genuino.crm.config.web;

import com.genuino.crm.config.ApprovalPolicyService;
import com.genuino.crm.config.domain.ApprovalPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.genuino.crm.config.dto.UpdateApprovalPolicyRequest;

@RestController
@RequestMapping("/api/config/approval-policies")
public class ApprovalPolicyController {

    private final ApprovalPolicyService approvalPolicyService;

    public ApprovalPolicyController(ApprovalPolicyService approvalPolicyService) {
        this.approvalPolicyService = approvalPolicyService;
    }

    @GetMapping("/{proformaType}")
    public ResponseEntity<ApprovalPolicy> getActivePolicy(@PathVariable String proformaType) {
        return ResponseEntity.ok(approvalPolicyService.getActivePolicy(proformaType));
    }

    @PutMapping("/{proformaType}")
    public ResponseEntity<ApprovalPolicy> updatePolicy(
            @PathVariable String proformaType,
            @RequestBody UpdateApprovalPolicyRequest request
    ) {
        return ResponseEntity.ok(approvalPolicyService.updatePolicy(proformaType, request));
    }
}