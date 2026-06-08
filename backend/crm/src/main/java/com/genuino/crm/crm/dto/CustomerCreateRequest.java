package com.genuino.crm.crm.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerCreateRequest(
        @NotBlank String name,
        String taxId,
        String email,
        String phone,
        String address,
        String ownerUserId
) {
}