package com.genuino.crm.config.web;

import com.genuino.crm.config.CalculationParameterService;
import com.genuino.crm.config.domain.CalculationParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parameters/calculation")
public class CalculationParameterController {

    private final CalculationParameterService service;

    public CalculationParameterController(
            CalculationParameterService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CalculationParameter>> findByScope(
            @RequestParam(defaultValue = "GENERAL") String scope,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(
                service.findByScope(scope, includeInactive)
        );
    }

    @PostMapping
    public ResponseEntity<CalculationParameter> create(
            @RequestBody CalculationParameter request
    ) {
        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalculationParameter> update(
            @PathVariable UUID id,
            @RequestBody CalculationParameter request
    ) {
        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id
    ) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CalculationParameter> activate(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                service.activate(id)
        );
    }
}