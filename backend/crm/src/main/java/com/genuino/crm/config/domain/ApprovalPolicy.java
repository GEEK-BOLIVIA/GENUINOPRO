package com.genuino.crm.config.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "approval_policy")
public class ApprovalPolicy {

    @Id
    private UUID id;

    private String proformaType;

    private BigDecimal supervisorLimit;

    private BigDecimal commercialManagerLimit;

    private String currency;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // getters y setters
    public BigDecimal getSupervisorLimit() {
        return supervisorLimit;
    }

    public BigDecimal getCommercialManagerLimit() {
        return commercialManagerLimit;
    }

    public void setSupervisorLimit(BigDecimal supervisorLimit) {
    this.supervisorLimit = supervisorLimit;
    }

    public void setCommercialManagerLimit(BigDecimal commercialManagerLimit) {
        this.commercialManagerLimit = commercialManagerLimit;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}