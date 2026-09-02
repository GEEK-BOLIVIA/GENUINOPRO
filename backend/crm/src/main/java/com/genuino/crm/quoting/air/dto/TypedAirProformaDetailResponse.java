package com.genuino.crm.quoting.air.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TypedAirProformaDetailResponse {

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

    private String rejectionReason;

    private AirCalculationRequest input;
    private AirCalculationResponse calculation;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(
            String rejectionReason
    ) {
        this.rejectionReason = rejectionReason;
    }

    public AirCalculationRequest getInput() {
        return input;
    }

    public void setInput(
            AirCalculationRequest input
    ) {
        this.input = input;
    }

    public AirCalculationResponse getCalculation() {
        return calculation;
    }

    public void setCalculation(
            AirCalculationResponse calculation
    ) {
        this.calculation = calculation;
    }
}