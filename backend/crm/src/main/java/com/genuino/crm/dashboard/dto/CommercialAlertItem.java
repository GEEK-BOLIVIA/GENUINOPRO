package com.genuino.crm.dashboard.dto;

import java.time.Instant;

public record CommercialAlertItem(
        String type,
        String entityId,
        String message,
        String ownerUserId,
        Instant createdAt,
        long hoursWithoutAction
) {
}