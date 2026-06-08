package com.genuino.crm.crm.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record CustomerProfile360Response(
        String customerId,
        String customerName,
        String status,
        String email,
        String phone,
        String address,
        CustomerSummaryResponse summary,
        String currentCommercialStatus,
        Instant lastActivityAt,
        List<Map<String, Object>> opportunities,
        List<Map<String, Object>> timeline
) {
}