package com.genuino.crm.quoting.web;

import com.genuino.crm.quoting.ProformaService;
import com.genuino.crm.quoting.domain.Proforma;
import com.genuino.crm.quoting.dto.DecisionRequest;
import com.genuino.crm.quoting.dto.ProformaCreateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proformas")
public class ProformaController {

    private final ProformaService service;

    public ProformaController(ProformaService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PostMapping
    public Proforma create(@Valid @RequestBody ProformaCreateRequest req) {
        return service.create(req);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @GetMapping("/{id}")
    public Proforma get(@PathVariable String id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','GERENCIA','ADMIN')")
    @PostMapping("/{id}/submit-review")
    public Proforma submitReview(@PathVariable String id) {
        return service.submitReview(id);
    }

    @PreAuthorize("hasAnyRole('GERENCIA','ADMIN')")
    @PostMapping("/{id}/approve")
    public Proforma approve(@PathVariable String id, @Valid @RequestBody DecisionRequest req) {
        return service.approve(id, req.reason());
    }

    @PreAuthorize("hasAnyRole('GERENCIA','ADMIN')")
    @PostMapping("/{id}/reject")
    public Proforma reject(@PathVariable String id, @Valid @RequestBody DecisionRequest req) {
        return service.reject(id, req.reason());
    }
}