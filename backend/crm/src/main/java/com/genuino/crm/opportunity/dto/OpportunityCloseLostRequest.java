package com.genuino.crm.opportunity.dto;

import jakarta.validation.constraints.NotBlank;

public record OpportunityCloseLostRequest(
        @NotBlank String reason
) {
}