package com.genuino.crm.account.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeMyPasswordRequest(
        @NotBlank String newPassword
) {}