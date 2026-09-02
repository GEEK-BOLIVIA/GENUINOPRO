package com.genuino.crm.quoting.air.web;

import com.genuino.crm.quoting.air.dto.AirCalculationRequest;
import com.genuino.crm.quoting.air.dto.AirCalculationResponse;
import com.genuino.crm.quoting.air.dto.CreateTypedAirProformaRequest;
import com.genuino.crm.quoting.air.dto.TypedAirProformaDetailResponse;

import com.genuino.crm.quoting.air.service.AirCalculationService;
import com.genuino.crm.quoting.air.service.TypedAirProformaService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/typed-proformas/air")
public class AirCalculationController {

    private final AirCalculationService calculationService;
    private final TypedAirProformaService proformaService;

    public AirCalculationController(
            AirCalculationService calculationService,
            TypedAirProformaService proformaService
    ) {
        this.calculationService =
                calculationService;

        this.proformaService =
                proformaService;
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @PostMapping("/calculate")
    public ResponseEntity<AirCalculationResponse> calculate(
            @RequestBody AirCalculationRequest request
    ) {

        return ResponseEntity.ok(
                calculationService.calculate(
                        request
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @PostMapping
    public ResponseEntity<TypedAirProformaDetailResponse> create(
            @RequestBody CreateTypedAirProformaRequest request
    ) {

        return ResponseEntity.ok(
                proformaService.create(
                        request
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @GetMapping
    public ResponseEntity<List<TypedAirProformaDetailResponse>> findAll() {

        return ResponseEntity.ok(
                proformaService.findAll()
        );
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TypedAirProformaDetailResponse> getById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                proformaService.getById(
                        id
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TypedAirProformaDetailResponse> update(
            @PathVariable UUID id,
            @RequestBody AirCalculationRequest request
    ) {

        return ResponseEntity.ok(
                proformaService.update(
                        id,
                        request
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('VENDEDOR','ADMIN','OWNER')"
    )
    @PostMapping("/{id}/submit-review")
    public ResponseEntity<TypedAirProformaDetailResponse> submitForReview(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                proformaService.submitForReview(
                        id
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN','OWNER')"
    )
    @PostMapping("/{id}/approve")
    public ResponseEntity<TypedAirProformaDetailResponse> approve(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                proformaService.approve(
                        id
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN','OWNER')"
    )
    @PostMapping("/{id}/reject")
    public ResponseEntity<TypedAirProformaDetailResponse> reject(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {

        return ResponseEntity.ok(
                proformaService.reject(
                        id,
                        body.get("reason")
                )
        );
    }
}