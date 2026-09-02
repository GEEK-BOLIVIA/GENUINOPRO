package com.genuino.crm.quoting.hbl.dto;

public class CreateTypedHblProformaRequest extends HblCalculationRequest {

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