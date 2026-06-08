package com.genuino.crm.opportunity.dto;

import java.time.LocalDateTime;

public class OpportunityTimelineItemResponse {

    private Long id;
    private String leadId;
    private String type;
    private String title;
    private String description;
    private LocalDateTime activityDate;
    private String source;
    private String createdBy;

    public OpportunityTimelineItemResponse() {
    }

    public OpportunityTimelineItemResponse(
            Long id,
            String leadId,
            String type,
            String title,
            String description,
            LocalDateTime activityDate,
            String source,
            String createdBy
    ) {
        this.id = id;
        this.leadId = leadId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.activityDate = activityDate;
        this.source = source;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getLeadId() {
        return leadId;
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

    public String getSource() {
        return source;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
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

    public void setSource(String source) {
        this.source = source;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}