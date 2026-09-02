package com.genuino.crm.quoting.hbl.web;

import com.genuino.crm.quoting.hbl.dto.CreateTypedHblProformaRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationResponse;
import com.genuino.crm.quoting.hbl.dto.TypedHblProformaDetailResponse;
import com.genuino.crm.quoting.hbl.pdf.TypedHblPdfService;
import com.genuino.crm.quoting.hbl.service.HblCalculationService;
import com.genuino.crm.quoting.hbl.service.TypedHblProformaService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/typed-proformas/hbl")
public class HblCalculationController {

    private final HblCalculationService calculationService;
    private final TypedHblProformaService proformaService;
    private final TypedHblPdfService pdfService;

    public HblCalculationController(
            HblCalculationService calculationService,
            TypedHblProformaService proformaService,
            TypedHblPdfService pdfService
    ) {
        this.calculationService = calculationService;
        this.proformaService = proformaService;
        this.pdfService = pdfService;
    }

    // =========================================================
    // CALCULAR
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping("/calculate")
    public ResponseEntity<HblCalculationResponse> calculate(
            @RequestBody HblCalculationRequest request
    ) {
        return ResponseEntity.ok(
                calculationService.calculate(request)
        );
    }

    // =========================================================
    // CREAR
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping
    public ResponseEntity<TypedHblProformaDetailResponse> create(
            @RequestBody CreateTypedHblProformaRequest request
    ) {
        return ResponseEntity.ok(
                proformaService.create(request)
        );
    }

    // =========================================================
    // LISTAR
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @GetMapping
    public ResponseEntity<List<TypedHblProformaDetailResponse>> findAll() {
        return ResponseEntity.ok(
                proformaService.findAll()
        );
    }

    // =========================================================
    // DETALLE
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @GetMapping("/{id}")
    public ResponseEntity<TypedHblProformaDetailResponse> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                proformaService.getById(id)
        );
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PutMapping("/{id}")
    public ResponseEntity<TypedHblProformaDetailResponse> update(
            @PathVariable UUID id,
            @RequestBody HblCalculationRequest request
    ) {
        return ResponseEntity.ok(
                proformaService.update(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // ENVIAR A REVISIÓN
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping("/{id}/submit-review")
    public ResponseEntity<TypedHblProformaDetailResponse> submitForReview(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                proformaService.submitForReview(id)
        );
    }

    // =========================================================
    // APROBAR
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<TypedHblProformaDetailResponse> approve(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                proformaService.approve(id)
        );
    }

    // =========================================================
    // RECHAZAR
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<TypedHblProformaDetailResponse> reject(
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

    // =========================================================
    // PDF
    // =========================================================

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable UUID id
    ) {

        TypedHblProformaDetailResponse detail =
                proformaService.getById(id);

        byte[] pdf =
                pdfService.generate(detail);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"proforma-HBL-"
                                + id
                                + ".pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}