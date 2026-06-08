package com.genuino.crm.commercialsummary.dto;

import java.math.BigDecimal;

public record CommercialSummaryMetricsResponse(
        long taskCount,
        long pendingTaskCount,
        long overdueTaskCount,
        long proformaCount,
        long approvedProformaCount,
        long rejectedProformaCount,
        BigDecimal totalQuotedAmount,
        BigDecimal estimatedProfit,
        Object lastActivityAt,
        Object nextTaskDueAt
) {}
