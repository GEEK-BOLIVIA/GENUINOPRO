package com.genuino.crm.commercialsummary.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommercialSummaryProformaResponse(
        UUID id,
        String opportunityId,
        String customerId,
        String type,
        String status,
        String currency,
        BigDecimal total,
        BigDecimal estimatedProfit,
        Integer version,
        String createdBy,
        LocalDateTime createdAt,
        String approvedBy,
        LocalDateTime approvedAt,
        String rejectionReason
) {}
