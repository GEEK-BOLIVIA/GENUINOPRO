package com.genuino.crm.commercialsummary.dto;

import java.time.Instant;

public record CommercialSummaryRelatedLeadResponse(
        String id,
        String fullName,
        String phone,
        String messagePreview,
        String status,
        String source,
        String channel,
        String assignedSellerId,
        Instant receivedAt,
        Instant createdAt,
        Integer year,
        Integer month,
        String monthLabel
) {}