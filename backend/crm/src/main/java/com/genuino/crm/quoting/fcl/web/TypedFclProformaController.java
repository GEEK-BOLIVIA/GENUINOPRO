package com.genuino.crm.quoting.fcl.web;

import com.genuino.crm.quoting.fcl.TypedFclProformaService;
import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.genuino.crm.quoting.fcl.pdf.TypedFclPdfService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

import com.genuino.crm.quoting.common.service.ProformaAttachmentService;
import com.genuino.crm.quoting.common.domain.ProformaAttachment;

import com.genuino.crm.quoting.fcl.dto.TypedFclProformaDetailResponse;

@RestController
@RequestMapping("/api/typed-proformas/fcl")
public class TypedFclProformaController {

    private final TypedFclProformaService service;
    private final TypedFclPdfService pdfService;
    private final ProformaAttachmentService attachmentService;

    public TypedFclProformaController(
            TypedFclProformaService service,
            TypedFclPdfService pdfService,
            ProformaAttachmentService attachmentService
    ) {
        this.service = service;
        this.pdfService = pdfService;
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<List<TypedFclProforma>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

        @GetMapping("/{id}")
        public ResponseEntity<TypedFclProformaDetailResponse> findById(
                @PathVariable UUID id
        ) {
        return ResponseEntity.ok(
                service.getDetail(id)
        );
        }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable UUID id
    ) {
        TypedFclProforma detail = service.findById(id);

            List<ProformaAttachment> attachments =
                    attachmentService.findByProforma(id);

            byte[] pdf = pdfService.generate(
                    detail,
                    attachments
            );

        String filename = "proforma-fcl-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping
    public ResponseEntity<TypedFclProforma> create(@RequestBody TypedFclProforma request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PostMapping("/calculate")
    public ResponseEntity<TypedFclProforma> calculate(
            @RequestBody TypedFclProforma request
    ) {
        return ResponseEntity.ok(service.calculate(request));
    }

    @PostMapping("/{id}/submit-review")
    public ResponseEntity<TypedFclProforma> submitForReview(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.submitForReview(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TypedFclProforma> approve(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.approve(id));
    }

@PostMapping("/{id}/reject")
public ResponseEntity<TypedFclProforma> reject(
        @PathVariable UUID id,
        @RequestBody Map<String, String> body
) {
    return ResponseEntity.ok(
            service.reject(
                    id,
                    body.get("reason")
            )
    );
}

    @PostMapping("/{id}/approve-customer")
    public ResponseEntity<TypedFclProforma> approveByCustomer(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                service.approveByCustomer(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypedFclProforma> update(
            @PathVariable UUID id,
            @RequestBody TypedFclProforma request
    ) {
        return ResponseEntity.ok(
                service.update(id, request)
        );
    }
}