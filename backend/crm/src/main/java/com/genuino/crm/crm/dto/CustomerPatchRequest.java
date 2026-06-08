package com.genuino.crm.crm.dto;

public record CustomerPatchRequest(
        String name,
        String taxId,
        String email,
        String phone,
        String address,
        String ownerUserId,
        String reason
) {
}