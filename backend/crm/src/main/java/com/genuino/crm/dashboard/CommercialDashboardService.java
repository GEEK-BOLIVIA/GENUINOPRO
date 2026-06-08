package com.genuino.crm.dashboard;

import com.genuino.crm.dashboard.dto.CommercialDashboardResponse;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.domain.Proforma;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genuino.crm.dashboard.dto.SellerRevenueItem;
import com.genuino.crm.dashboard.dto.SellerRevenueDashboardResponse;

import java.util.Map;
import java.util.HashMap;

import com.genuino.crm.dashboard.dto.DailyRevenueItem;
import com.genuino.crm.dashboard.dto.PremiumRevenueDashboardResponse;

import com.genuino.crm.dashboard.dto.CommercialAlertItem;
import com.genuino.crm.dashboard.dto.CommercialAlertsResponse;

import com.genuino.crm.inbox.domain.LeadInbox;

import java.time.Duration;
import java.time.Instant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.TreeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommercialDashboardService {

    private final LeadInboxRepository leadInboxRepository;
    private final OpportunityRepository opportunityRepository;
    private final ProformaRepository proformaRepository;

    public CommercialDashboardService(
            LeadInboxRepository leadInboxRepository,
            OpportunityRepository opportunityRepository,
            ProformaRepository proformaRepository
    ) {
        this.leadInboxRepository = leadInboxRepository;
        this.opportunityRepository = opportunityRepository;
        this.proformaRepository = proformaRepository;
    }

    @Transactional(readOnly = true)
    public CommercialDashboardResponse getSummary() {

        long totalLeads = leadInboxRepository.count();

        List<Opportunity> opportunities = opportunityRepository.findAll();
        List<Proforma> proformas = proformaRepository.findAll();

        long totalOpportunities = opportunities.size();
        long totalWon = opportunities.stream().filter(o -> "WON".equals(o.stage)).count();
        long totalLost = opportunities.stream().filter(o -> "LOST".equals(o.stage)).count();

        long totalProformas = proformas.size();
        long totalApprovedProformas = proformas.stream()
                .filter(p -> "APPROVED".equals(p.status))
                .count();

        Map<String, Long> opportunitiesByStage = new HashMap<>();
        for (Opportunity o : opportunities) {
            opportunitiesByStage.merge(o.stage, 1L, Long::sum);
        }

        return new CommercialDashboardResponse(
                totalLeads,
                totalOpportunities,
                totalWon,
                totalLost,
                totalProformas,
                totalApprovedProformas,
                opportunitiesByStage
        );
    }

    @Transactional(readOnly = true)
    public java.util.List<com.genuino.crm.dashboard.dto.SellerDashboardItem> getBySeller() {

        java.util.List<com.genuino.crm.inbox.domain.LeadInbox> leads = leadInboxRepository.findAll();
        java.util.List<com.genuino.crm.opportunity.domain.Opportunity> opportunities = opportunityRepository.findAll();

        java.util.Map<String, Long> leadsBySeller = new java.util.HashMap<>();
        java.util.Map<String, Long> oppBySeller = new java.util.HashMap<>();
        java.util.Map<String, Long> wonBySeller = new java.util.HashMap<>();
        java.util.Map<String, Long> lostBySeller = new java.util.HashMap<>();

        for (var lead : leads) {
            if (lead.assignedSellerId != null) {
                leadsBySeller.merge(lead.assignedSellerId, 1L, Long::sum);
            }
        }

        for (var opp : opportunities) {
            if (opp.ownerUserId != null) {
                oppBySeller.merge(opp.ownerUserId, 1L, Long::sum);

                if ("WON".equals(opp.stage)) {
                    wonBySeller.merge(opp.ownerUserId, 1L, Long::sum);
                }

                if ("LOST".equals(opp.stage)) {
                    lostBySeller.merge(opp.ownerUserId, 1L, Long::sum);
                }
            }
        }

        java.util.Set<String> sellerIds = new java.util.HashSet<>();
        sellerIds.addAll(leadsBySeller.keySet());
        sellerIds.addAll(oppBySeller.keySet());
        sellerIds.addAll(wonBySeller.keySet());
        sellerIds.addAll(lostBySeller.keySet());

        java.util.List<com.genuino.crm.dashboard.dto.SellerDashboardItem> result = new java.util.ArrayList<>();

        for (String sellerId : sellerIds) {
            result.add(new com.genuino.crm.dashboard.dto.SellerDashboardItem(
                    sellerId,
                    leadsBySeller.getOrDefault(sellerId, 0L),
                    oppBySeller.getOrDefault(sellerId, 0L),
                    wonBySeller.getOrDefault(sellerId, 0L),
                    lostBySeller.getOrDefault(sellerId, 0L)
            ));
        }

        result.sort(java.util.Comparator.comparing(com.genuino.crm.dashboard.dto.SellerDashboardItem::sellerId));
        return result;
    }

    @Transactional(readOnly = true)
    public com.genuino.crm.dashboard.dto.CommercialConversionResponse getConversion() {

        long totalLeads = leadInboxRepository.count();
        long totalOpportunities = opportunityRepository.count();

        long totalWon = opportunityRepository.findAll().stream()
                .filter(o -> "WON".equals(o.stage))
                .count();

        double leadToOpportunityRate = totalLeads == 0
                ? 0.0
                : round2((totalOpportunities * 100.0) / totalLeads);

        double opportunityToWonRate = totalOpportunities == 0
                ? 0.0
                : round2((totalWon * 100.0) / totalOpportunities);

        double leadToWonRate = totalLeads == 0
                ? 0.0
                : round2((totalWon * 100.0) / totalLeads);

        return new com.genuino.crm.dashboard.dto.CommercialConversionResponse(
                totalLeads,
                totalOpportunities,
                totalWon,
                leadToOpportunityRate,
                opportunityToWonRate,
                leadToWonRate
        );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Transactional(readOnly = true)
    public PremiumRevenueDashboardResponse getPremiumRevenue() {

    List<Proforma> proformas = proformaRepository.findAll();
    List<Opportunity> opportunities = opportunityRepository.findAll();

    List<Proforma> approvedProformas = proformas.stream()
            .filter(p -> "APPROVED".equals(p.status))
            .toList();

    BigDecimal totalApprovedRevenue = approvedProformas.stream()
            .map(p -> p.total != null ? p.total : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    long totalApprovedProformas = approvedProformas.size();

    long totalWonOpportunities = opportunities.stream()
            .filter(o -> "WON".equals(o.stage))
            .count();

    BigDecimal averageTicket = totalApprovedProformas == 0
            ? BigDecimal.ZERO
            : totalApprovedRevenue.divide(
                    BigDecimal.valueOf(totalApprovedProformas),
                    2,
                    java.math.RoundingMode.HALF_UP
            );

    TreeMap<LocalDate, BigDecimal> revenueByDay = new TreeMap<>();
    TreeMap<LocalDate, Long> approvedByDay = new TreeMap<>();
    TreeMap<LocalDate, Long> wonByDay = new TreeMap<>();

    for (Proforma p : approvedProformas) {
        if (p.approvedAt != null) {
            LocalDate day = p.approvedAt.atZone(ZoneOffset.UTC).toLocalDate();
            revenueByDay.merge(day, p.total != null ? p.total : BigDecimal.ZERO, BigDecimal::add);
            approvedByDay.merge(day, 1L, Long::sum);
        }
    }

    for (Opportunity o : opportunities) {
        if ("WON".equals(o.stage) && o.updatedAt != null) {
            LocalDate day = o.updatedAt.atZone(ZoneOffset.UTC).toLocalDate();
            wonByDay.merge(day, 1L, Long::sum);
        }
    }

    java.util.Set<LocalDate> allDays = new java.util.TreeSet<>();
    allDays.addAll(revenueByDay.keySet());
    allDays.addAll(approvedByDay.keySet());
    allDays.addAll(wonByDay.keySet());

    List<DailyRevenueItem> dailyTrend = new ArrayList<>();
    for (LocalDate day : allDays) {
        dailyTrend.add(new DailyRevenueItem(
                day,
                revenueByDay.getOrDefault(day, BigDecimal.ZERO),
                approvedByDay.getOrDefault(day, 0L),
                wonByDay.getOrDefault(day, 0L)
        ));
    }

    return new PremiumRevenueDashboardResponse(
            totalApprovedRevenue,
            totalApprovedProformas,
            totalWonOpportunities,
            averageTicket,
            dailyTrend
    );
    }


    @Transactional(readOnly = true)
    public SellerRevenueDashboardResponse getRevenueBySeller() {

        List<Proforma> proformas = proformaRepository.findAll();
        List<Opportunity> opportunities = opportunityRepository.findAll();

        Map<String, BigDecimal> revenueBySeller = new HashMap<>();
        Map<String, Long> approvedBySeller = new HashMap<>();
        Map<String, Long> wonBySeller = new HashMap<>();

        // Proformas aprobadas → ingresos por vendedor
        for (Proforma p : proformas) {
            if ("APPROVED".equals(p.status) && p.opportunityId != null) {

                Opportunity o = opportunities.stream()
                        .filter(op -> op.id.equals(p.opportunityId))
                        .findFirst()
                        .orElse(null);

                if (o != null && o.ownerUserId != null) {

                    String seller = o.ownerUserId;

                    revenueBySeller.merge(
                            seller,
                            p.total != null ? p.total : BigDecimal.ZERO,
                            BigDecimal::add
                    );

                    approvedBySeller.merge(seller, 1L, Long::sum);
                }
            }
        }

        // Oportunidades WON por vendedor
        for (Opportunity o : opportunities) {
            if ("WON".equals(o.stage) && o.ownerUserId != null) {
                wonBySeller.merge(o.ownerUserId, 1L, Long::sum);
            }
        }

        // Construcción final
        List<SellerRevenueItem> result = new java.util.ArrayList<>();

        for (String seller : revenueBySeller.keySet()) {

            BigDecimal revenue = revenueBySeller.getOrDefault(seller, BigDecimal.ZERO);
            long approved = approvedBySeller.getOrDefault(seller, 0L);
            long won = wonBySeller.getOrDefault(seller, 0L);

            BigDecimal avg = approved == 0
                    ? BigDecimal.ZERO
                    : revenue.divide(
                            BigDecimal.valueOf(approved),
                            2,
                            java.math.RoundingMode.HALF_UP
                    );

            result.add(new SellerRevenueItem(
                    seller,
                    revenue,
                    approved,
                    won,
                    avg
            ));
        }

        return new SellerRevenueDashboardResponse(result);
    }

    @Transactional(readOnly = true)
    public CommercialAlertsResponse getAlerts() {

        List<CommercialAlertItem> alerts = new java.util.ArrayList<>();

        List<LeadInbox> leads = leadInboxRepository.findAll();
        List<Opportunity> opportunities = opportunityRepository.findAll();
        List<Proforma> proformas = proformaRepository.findAll();

        Instant now = Instant.now();

        // 🚨 1. Leads sin contactar (> 2 horas)
        for (LeadInbox l : leads) {
            if ("NEW".equals(l.status) && l.createdAt != null) {

                long hours = Duration.between(l.createdAt, now).toHours();

                if (hours >= 2) {
                    alerts.add(new CommercialAlertItem(
                            "LEAD_NO_CONTACT",
                            l.id,
                            "Lead sin contactar",
                            l.assignedSellerId,
                            l.createdAt,
                            hours
                    ));
                }
            }
        }

        // 🚨 2. Oportunidades estancadas (> 24 horas)
        for (Opportunity o : opportunities) {

            if (o.updatedAt != null &&
                    ("CONTACTED".equals(o.stage) || "PROPOSAL".equals(o.stage))) {

                long hours = Duration.between(o.updatedAt, now).toHours();

                if (hours >= 24) {
                    alerts.add(new CommercialAlertItem(
                            "OPPORTUNITY_STALLED",
                            o.id,
                            "Oportunidad sin avance",
                            o.ownerUserId,
                            o.updatedAt,
                            hours
                    ));
                }
            }
        }

        // 🚨 3. Proformas en DRAFT (> 24 horas)
        for (Proforma p : proformas) {

            if ("DRAFT".equals(p.status) && p.createdAt != null) {

                long hours = Duration.between(p.createdAt, now).toHours();

                if (hours >= 24) {
                    alerts.add(new CommercialAlertItem(
                            "PROFORMA_DRAFT_STALLED",
                            p.id,
                            "Proforma en borrador sin enviar",
                            p.createdBy,
                            p.createdAt,
                            hours
                    ));
                }
            }
        }

        // 🚨 4. Proformas en revisión (> 24 horas)
        for (Proforma p : proformas) {

            if ("IN_REVIEW".equals(p.status) && p.submittedAt != null) {

                long hours = Duration.between(p.submittedAt, now).toHours();

                if (hours >= 24) {
                    alerts.add(new CommercialAlertItem(
                            "PROFORMA_REVIEW_DELAY",
                            p.id,
                            "Proforma en revisión sin aprobar",
                            p.createdBy,
                            p.submittedAt,
                            hours
                    ));
                }
            }
        }

        return new CommercialAlertsResponse(alerts);
    }

}