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

    @GetMapping
    public ResponseEntity<List<ProformaRate>> findByType(
            @RequestParam(defaultValue = "LCL") String proformaType
    ) {
        return ResponseEntity.ok(service.findByType(proformaType));
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
}