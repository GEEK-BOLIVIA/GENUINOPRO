package com.genuino.crm.opportunity.dto;

public record OpportunityConvertToCustomerResponse(
        String opportunityId,
        String customerId,
        String customerName,
        String status
) {
}