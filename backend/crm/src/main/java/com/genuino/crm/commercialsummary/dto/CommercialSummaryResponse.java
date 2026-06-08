package com.genuino.crm.commercialsummary.dto;

import java.util.List;

public record CommercialSummaryResponse(
        CommercialSummaryLeadResponse lead,
        CommercialSummaryOpportunityResponse opportunity,
        List<CommercialSummaryRelatedLeadResponse> relatedLeads,
        List<CommercialSummaryTimelineItemResponse> timeline,
        List<CommercialSummaryTaskResponse> tasks,
        List<CommercialSummaryProformaResponse> proformas,
        List<CommercialSummaryProformaGroupResponse> proformaGroups,
        CommercialSummaryMetricsResponse metrics
) {}