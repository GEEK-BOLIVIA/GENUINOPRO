package com.genuino.crm.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateOpportunityActivityRequest {

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String type;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    private LocalDateTime activityDate;

    public CreateOpportunityActivityRequest() {
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getActivityDate() {
        return activityDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActivityDate(LocalDateTime activityDate) {
        this.activityDate = activityDate;
    }
}