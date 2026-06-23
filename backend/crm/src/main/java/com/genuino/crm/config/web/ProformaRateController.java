package com.genuino.crm.config.web;

import com.genuino.crm.config.ProformaRateService;
import com.genuino.crm.config.domain.ProformaRate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parameters/proforma-rates")
public class ProformaRateController {

    private final ProformaRateService service;

    public ProformaRateController(ProformaRateService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProformaRate> activate(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProformaRate> update(
            @PathVariable UUID id,
            @RequestBody ProformaRate request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping
    public ResponseEntity<ProformaRate> create(
            @RequestBody ProformaRate request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProformaRate>> findByType(
            @RequestParam(defaultValue = "LCL") String proformaType,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(
                service.findByType(proformaType, includeInactive)
        );
    }
}