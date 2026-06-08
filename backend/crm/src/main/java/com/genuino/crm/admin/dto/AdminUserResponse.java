package com.genuino.crm.admin.dto;
public record AdminUserResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String role
) {}