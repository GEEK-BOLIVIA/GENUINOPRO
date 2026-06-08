package com.genuino.crm.config.dto;

import java.math.BigDecimal;

public class UpdateApprovalPolicyRequest {

    private BigDecimal supervisorLimit;
    private BigDecimal commercialManagerLimit;

    public BigDecimal getSupervisorLimit() {
        return supervisorLimit;
    }

    public void setSupervisorLimit(BigDecimal supervisorLimit) {
        this.supervisorLimit = supervisorLimit;
    }

    public BigDecimal getCommercialManagerLimit() {
        return commercialManagerLimit;
    }

    public void setCommercialManagerLimit(BigDecimal commercialManagerLimit) {
        this.commercialManagerLimit = commercialManagerLimit;
    }
}