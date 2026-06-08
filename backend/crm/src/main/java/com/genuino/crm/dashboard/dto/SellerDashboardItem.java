package com.genuino.crm.dashboard.dto;

public record SellerDashboardItem(
        String sellerId,
        long leads,
        long opportunities,
        long won,
        long lost
) {
}