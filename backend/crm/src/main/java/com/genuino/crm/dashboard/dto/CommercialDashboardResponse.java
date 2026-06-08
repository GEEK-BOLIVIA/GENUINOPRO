package com.genuino.crm.dashboard.dto;

import java.util.Map;

public record CommercialDashboardResponse(
        long totalLeads,
        long totalOpportunities,
        long totalWon,
        long totalLost,
        long totalProformas,
        long totalApprovedProformas,
        Map<String, Long> opportunitiesByStage
) {
}