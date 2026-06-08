package com.genuino.crm.crm.dto;

import java.util.List;
import java.util.Map;

public record CustomerTimelineResponse(
        String customerId,
        String customerName,
        String status,
        List<Map<String, Object>> events
) {
}