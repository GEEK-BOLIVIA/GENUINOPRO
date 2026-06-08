package com.genuino.crm.commercialsummary.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommercialSummaryTaskResponse(
        UUID id,
        String leadId,
        String opportunityId,
        UUID proformaId,
        String title,
        String description,
        String status,
        String priority,
        String assignedTo,
        OffsetDateTime dueAt,
        OffsetDateTime createdAt,
        OffsetDateTime completedAt
) {}