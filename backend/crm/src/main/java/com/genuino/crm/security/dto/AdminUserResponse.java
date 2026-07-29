package com.genuino.crm.security.dto;
public record AdminUserResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String role
) {}