package com.genuino.crm.opportunity.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpportunityTimelineResponse {

    private String leadId;
    private String customerName;
    private String stage;
    private String status;
    private List<Map<String, Object>> events;
    private List<OpportunityTimelineItemResponse> activities;

    public OpportunityTimelineResponse() {
        this.events = new ArrayList<>();
        this.activities = new ArrayList<>();
    }

    // Constructor compatible con el código viejo
    public OpportunityTimelineResponse(
            String leadId,
            String customerName,
            String stage,
            String status,
            List<Map<String, Object>> events
    ) {
        this.leadId = leadId;
        this.customerName = customerName;
        this.stage = stage;
        this.status = status;
        this.events = events != null ? events : new ArrayList<>();
        this.activities = new ArrayList<>();
    }

    // Constructor útil para actividades manuales si lo necesitas
    public OpportunityTimelineResponse(List<OpportunityTimelineItemResponse> activities) {
        this.events = new ArrayList<>();
        this.activities = activities != null ? activities : new ArrayList<>();
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, Object>> getEvents() {
        return events;
    }

    public void setEvents(List<Map<String, Object>> events) {
        this.events = events;
    }

    public List<OpportunityTimelineItemResponse> getActivities() {
        return activities;
    }

    public void setActivities(List<OpportunityTimelineItemResponse> activities) {
        this.activities = activities;
    }
}