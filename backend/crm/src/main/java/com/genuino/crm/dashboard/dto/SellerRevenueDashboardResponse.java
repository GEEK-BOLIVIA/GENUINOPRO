package com.genuino.crm.dashboard.dto;

import java.util.List;

public record SellerRevenueDashboardResponse(
        List<SellerRevenueItem> sellers
) {
}