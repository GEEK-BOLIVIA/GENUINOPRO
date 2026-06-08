package com.genuino.crm.dashboard.dto;

import java.math.BigDecimal;

public record SellerRevenueItem(
        String sellerId,
        BigDecimal approvedRevenue,
        long approvedProformas,
        long wonOpportunities,
        BigDecimal averageTicket
) {
}