package com.genuino.crm.commercialsummary.dto;

import java.time.Instant;

public record CommercialSummaryLeadResponse(
        String id,
        String fullName,
        String phone,
        String source,
        String channel,
        String status,
        String assignedSellerId,
        String messagePreview,
        Instant receivedAt,
        Instant createdAt
) {}
