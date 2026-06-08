package com.genuino.crm.crm.web;

import com.genuino.crm.audit.AuditService;
import com.genuino.crm.crm.CustomerProfile360Service;
import com.genuino.crm.crm.CustomerSummaryService;
import com.genuino.crm.crm.CustomerTimelineService;
import com.genuino.crm.crm.domain.Customer;
import com.genuino.crm.crm.dto.CustomerCreateRequest;
import com.genuino.crm.crm.dto.CustomerPatchRequest;
import com.genuino.crm.crm.dto.CustomerProfile360Response;
import com.genuino.crm.crm.dto.CustomerSummaryResponse;
import com.genuino.crm.crm.dto.CustomerTimelineResponse;
import com.genuino.crm.crm.infra.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repo;
    private final AuditService auditService;
    private final CustomerTimelineService timelineService;
    private final CustomerSummaryService summaryService;
    private final CustomerProfile360Service profile360Service;

    public CustomerController(
            CustomerRepository repo,
            AuditService auditService,
            CustomerTimelineService timelineService,
            CustomerSummaryService summaryService,
            CustomerProfile360Service profile360Service
    ) {
        this.repo = repo;
        this.auditService = auditService;
        this.timelineService = timelineService;
        this.summaryService = summaryService;
        this.profile360Service = profile360Service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @PostMapping
    public Customer create(@Valid @RequestBody CustomerCreateRequest req) {
        Customer c = new Customer();
        c.id = "cus_" + UUID.randomUUID();
        c.name = req.name();
        c.taxId = req.taxId();
        c.email = req.email();
        c.phone = req.phone();
        c.address = req.address();
        c.ownerUserId = req.ownerUserId();

        Customer saved = repo.save(c);
        auditService.log("CREATE", "CUSTOMER", saved.id, null, saved, null, "SUCCESS", null);
        return saved;
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @GetMapping("/{id}")
    public Customer getById(@PathVariable String id) {
        return repo.findById(id).orElseThrow();
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @PatchMapping("/{id}")
    public Customer patch(@PathVariable String id, @RequestBody CustomerPatchRequest req) {
        Customer c = repo.findById(id).orElseThrow();
        Customer before = cloneCustomer(c);

        boolean sensitiveChanged =
                (req.name() != null && !req.name().equals(c.name)) ||
                (req.taxId() != null && !req.taxId().equals(c.taxId));

        if (sensitiveChanged && (req.reason() == null || req.reason().isBlank())) {
            throw new IllegalArgumentException("reason is required for sensitive changes");
        }

        if (req.name() != null) c.name = req.name();
        if (req.taxId() != null) c.taxId = req.taxId();
        if (req.email() != null) c.email = req.email();
        if (req.phone() != null) c.phone = req.phone();
        if (req.address() != null) c.address = req.address();
        if (req.ownerUserId() != null) c.ownerUserId = req.ownerUserId();

        Customer saved = repo.save(c);
        auditService.log("UPDATE", "CUSTOMER", saved.id, before, saved, req.reason(), "SUCCESS", null);
        return saved;
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @GetMapping("/{id}/timeline")
    public CustomerTimelineResponse timeline(@PathVariable String id) {
        return timelineService.getTimeline(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @GetMapping("/{id}/summary")
    public CustomerSummaryResponse summary(@PathVariable String id) {
        return summaryService.getSummary(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENCIA','VENDEDOR')")
    @GetMapping("/{id}/profile-360")
    public CustomerProfile360Response profile360(@PathVariable String id) {
        return profile360Service.getProfile(id);
    }

    private Customer cloneCustomer(Customer c) {
        Customer copy = new Customer();
        copy.id = c.id;
        copy.name = c.name;
        copy.taxId = c.taxId;
        copy.email = c.email;
        copy.phone = c.phone;
        copy.address = c.address;
        copy.ownerUserId = c.ownerUserId;
        copy.status = c.status;
        copy.createdAt = c.createdAt;
        copy.updatedAt = c.updatedAt;
        return copy;
    }
}