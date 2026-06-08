package com.genuino.crm.inbox.dto;

import jakarta.validation.constraints.NotBlank;

public record WhapifyLeadRequest(
        String externalConversationId,
        String externalContactId,
        @NotBlank String phone,
        String fullName,
        String messagePreview,
        String channel,
        String payloadJson
) {
}