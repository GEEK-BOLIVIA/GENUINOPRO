package com.genuino.crm.activity;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadActivityService {

    private final LeadActivityRepository repository;

    public LeadActivityService(LeadActivityRepository repository) {
        this.repository = repository;
    }

    public List<LeadActivity> getLeadActivities(String leadId) {
        return repository.findByLeadIdOrderByCreatedAtDesc(leadId);
    }

    public LeadActivity createActivity(
            String leadId,
            String type,
            String description,
            String createdBy
    ) {
        LeadActivity activity = new LeadActivity();

        activity.leadId = leadId;
        activity.type = type;
        activity.description = description;
        activity.createdBy = createdBy;

        return repository.save(activity);
    }
}