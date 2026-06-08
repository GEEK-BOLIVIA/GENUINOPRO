package com.genuino.crm.account.dto;

public record MeResponse(
        String username,
        String email,
        String firstName,
        String lastName,
        String role
) {}