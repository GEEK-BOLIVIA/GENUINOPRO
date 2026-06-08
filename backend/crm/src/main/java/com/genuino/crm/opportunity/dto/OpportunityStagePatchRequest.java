package com.genuino.crm.opportunity.dto;

import jakarta.validation.constraints.NotBlank;

public record OpportunityStagePatchRequest(
        @NotBlank String stage,
        String reason
) {
}