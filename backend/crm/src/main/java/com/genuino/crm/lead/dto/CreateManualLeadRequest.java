package com.genuino.crm.lead.dto;

public record CreateManualLeadRequest(
        String fullName,
        String phone,
        String messagePreview,
        String assignedSellerId
) {
}