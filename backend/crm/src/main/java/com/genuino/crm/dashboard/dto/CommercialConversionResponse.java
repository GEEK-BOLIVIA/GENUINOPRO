package com.genuino.crm.dashboard.dto;

public record CommercialConversionResponse(
        long totalLeads,
        long totalOpportunities,
        long totalWon,
        double leadToOpportunityRate,
        double opportunityToWonRate,
        double leadToWonRate
) {
}