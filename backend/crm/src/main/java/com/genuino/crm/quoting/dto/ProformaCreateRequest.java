package com.genuino.crm.quoting.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProformaCreateRequest(
        @NotBlank String customerId,
        String opportunityId,
        @NotBlank String currency,
        BigDecimal amount
) {
}