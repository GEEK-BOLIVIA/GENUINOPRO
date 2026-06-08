package com.genuino.crm.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerSummaryResponse(
        String customerId,
        String customerName,
        String status,
        long totalProformas,
        long approvedProformas,
        long draftProformas,
        long inReviewProformas,
        BigDecimal totalQuotedAmount,
        BigDecimal totalApprovedAmount,
        Instant lastProformaAt,
        Instant lastApprovedProformaAt,
        long linkedOpportunities,
        long wonOpportunities
) {
}