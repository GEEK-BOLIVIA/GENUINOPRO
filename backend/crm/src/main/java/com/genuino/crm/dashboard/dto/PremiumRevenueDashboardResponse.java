package com.genuino.crm.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record PremiumRevenueDashboardResponse(
        BigDecimal totalApprovedRevenue,
        long totalApprovedProformas,
        long totalWonOpportunities,
        BigDecimal averageTicket,
        List<DailyRevenueItem> dailyTrend
) {
}