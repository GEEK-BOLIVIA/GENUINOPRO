package com.genuino.crm.dashboard.web;

import com.genuino.crm.dashboard.CommercialDashboardService;
import com.genuino.crm.dashboard.dto.CommercialConversionResponse;
import com.genuino.crm.dashboard.dto.CommercialDashboardResponse;
import com.genuino.crm.dashboard.dto.PremiumRevenueDashboardResponse;
import com.genuino.crm.dashboard.dto.SellerDashboardItem;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genuino.crm.dashboard.dto.CommercialAlertsResponse;

import com.genuino.crm.dashboard.dto.SellerRevenueDashboardResponse;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class CommercialDashboardController {

    private final CommercialDashboardService service;

    public CommercialDashboardController(CommercialDashboardService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial")
    public CommercialDashboardResponse commercial() {
        return service.getSummary();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial/revenue-by-seller")
    public SellerRevenueDashboardResponse revenueBySeller() {
        return service.getRevenueBySeller();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial/by-seller")
    public List<SellerDashboardItem> bySeller() {
        return service.getBySeller();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial/conversion")
    public CommercialConversionResponse conversion() {
        return service.getConversion();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial/premium-revenue")
    public PremiumRevenueDashboardResponse premiumRevenue() {
        return service.getPremiumRevenue();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/commercial/alerts")
    public CommercialAlertsResponse alerts() {
        return service.getAlerts();
    }
}