package com.genuino.crm.dashboard.dto;

import java.util.List;

public record CommercialAlertsResponse(
        List<CommercialAlertItem> alerts
) {
}