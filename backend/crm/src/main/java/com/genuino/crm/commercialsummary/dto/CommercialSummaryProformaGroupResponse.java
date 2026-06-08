package com.genuino.crm.commercialsummary.dto;

import java.util.List;

public record CommercialSummaryProformaGroupResponse(
        CommercialSummaryProformaResponse proforma,
        List<CommercialSummaryTimelineItemResponse> timeline,
        List<CommercialSummaryTaskResponse> tasks
) {}