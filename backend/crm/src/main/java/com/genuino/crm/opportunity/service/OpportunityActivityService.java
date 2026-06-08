package com.genuino.crm.opportunity.service;

import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.CreateOpportunityActivityRequest;
import com.genuino.crm.opportunity.dto.OpportunityTimelineItemResponse;
import com.genuino.crm.opportunity.infra.OpportunityActivityRepository;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.opportunity.model.OpportunityActivity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class OpportunityActivityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityActivityRepository opportunityActivityRepository;

    public OpportunityActivityService(
            OpportunityRepository opportunityRepository,
            OpportunityActivityRepository opportunityActivityRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.opportunityActivityRepository = opportunityActivityRepository;
    }

    @Transactional
    public OpportunityTimelineItemResponse createManualActivity(
            String leadId,
            CreateOpportunityActivityRequest request,
            String currentUser
    ) {
        Opportunity opportunity = opportunityRepository.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la oportunidad con id " + leadId));

        OpportunityActivity activity = new OpportunityActivity();
        activity.setOpportunityId(leadId);
        activity.setType(request.getType().trim().toUpperCase());
        activity.setTitle(request.getTitle().trim());
        activity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        activity.setActivityDate(request.getActivityDate() != null ? request.getActivityDate() : LocalDateTime.now());
        activity.setSource("MANUAL");
        activity.setCreatedBy(currentUser);
        activity.setCreatedAt(LocalDateTime.now());

        OpportunityActivity saved = opportunityActivityRepository.save(activity);

        return new OpportunityTimelineItemResponse(
                saved.getId(),
                saved.getOpportunityId(),
                saved.getType(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getActivityDate(),
                saved.getSource(),
                saved.getCreatedBy()
        );
    }


    @Transactional(readOnly = true)
    public List<OpportunityTimelineItemResponse> getTimeline(String leadId) {
        opportunityRepository.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la oportunidad con id " + leadId));

        return opportunityActivityRepository.findByOpportunityIdOrderByActivityDateDesc(leadId)
                .stream()
                .map(activity -> new OpportunityTimelineItemResponse(
                        activity.getId(),
                        activity.getOpportunityId(),
                        activity.getType(),
                        activity.getTitle(),
                        activity.getDescription(),
                        activity.getActivityDate(),
                        activity.getSource(),
                        activity.getCreatedBy()
                ))
                .toList();
    }
}