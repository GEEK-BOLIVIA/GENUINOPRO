package com.genuino.crm.audit.web;

import com.genuino.crm.audit.domain.AuditEvent;
import com.genuino.crm.audit.infra.AuditEventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditEventRepository repo;

    public AuditController(AuditEventRepository repo) {
        this.repo = repo;
    }

    @PreAuthorize("hasAnyRole('GERENCIA','ADMIN')")
    @GetMapping
    public List<AuditEvent> list() {
        return repo.findAll();
    }
}