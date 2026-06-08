package com.genuino.crm.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenueItem(
        LocalDate day,
        BigDecimal approvedRevenue,
        long approvedProformas,
        long wonOpportunities
) {
}