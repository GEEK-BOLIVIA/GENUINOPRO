package com.genuino.crm.quoting.hbl.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TypedHblProformaDetailResponse {

    private UUID id;
    private String type;
    private String status;
    private String currency;
    private Integer version;
    private String opportunityId;
    private String customerId;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private HblCalculationRequest input;
    private HblCalculationResponse calculation;
    private String rejectionReason;

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public HblCalculationRequest getInput() { return input; }
    public void setInput(HblCalculationRequest input) { this.input = input; }
    public HblCalculationResponse getCalculation() { return calculation; }
    public void setCalculation(HblCalculationResponse calculation) { this.calculation = calculation; }


}

