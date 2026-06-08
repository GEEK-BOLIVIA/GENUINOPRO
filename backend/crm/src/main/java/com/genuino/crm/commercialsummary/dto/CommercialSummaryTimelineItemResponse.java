package com.genuino.crm.commercialsummary.dto;

import java.util.Map;
import java.util.UUID;

public record CommercialSummaryTimelineItemResponse(
        String type,
        String category,
        String title,
        String description,
        Object timestamp,
        String source,
        String actorUserId,
        String opportunityId,
        UUID proformaId,
        UUID taskId,
        Map<String, Object> metadata
) {}
