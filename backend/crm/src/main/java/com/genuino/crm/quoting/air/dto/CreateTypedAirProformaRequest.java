package com.genuino.crm.quoting.air.dto;

public class CreateTypedAirProformaRequest
        extends AirCalculationRequest {

    private String notes;
    private String createdBy;

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
}