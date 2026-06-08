package com.genuino.crm.commercialsummary.dto;

import java.time.Instant;

public record CommercialSummaryOpportunityResponse(
        String id,
        String customerId,
        String leadInboxId,
        String title,
        String stage,
        String source,
        String ownerUserId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
