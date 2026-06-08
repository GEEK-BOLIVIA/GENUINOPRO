package com.genuino.crm.quoting.dto;

import jakarta.validation.constraints.NotBlank;

public record DecisionRequest(@NotBlank String reason) {
}