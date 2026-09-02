package com.genuino.crm.quoting.lcl.web;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.dto.RejectWorkflowActionRequest;
import com.genuino.crm.quoting.common.dto.WorkflowActionRequest;
import com.genuino.crm.quoting.lcl.dto.CreateTypedLclProformaRequest;
import com.genuino.crm.quoting.lcl.dto.RecalculateTypedLclProformaRequest;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaDetailResponse;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaResponse;
import com.genuino.crm.quoting.lcl.service.TypedLclProformaService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.genuino.crm.security.SecurityUserService;

import com.genuino.crm.quoting.lcl.pdf.TypedLclPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationRequest;
import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationResponse;
import com.genuino.crm.quoting.lcl.service.LclOperationalCalculationService;

import com.genuino.crm.quoting.lcl.service.TypedLclProformaService;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.service.ProformaAttachmentService;

@RestController
@RequestMapping("/api/typed-proformas/lcl")
public class TypedLclProformaController {

    private final TypedLclProformaService typedLclProformaService;
    private final SecurityUserService securityUserService;
    private final TypedLclPdfService typedLclPdfService;
    private final LclOperationalCalculationService lclOperationalCalculationService;
    private final ProformaAttachmentService attachmentService;

    public TypedLclProformaController(
            TypedLclProformaService typedLclProformaService,
            SecurityUserService securityUserService,
            TypedLclPdfService typedLclPdfService,
            LclOperationalCalculationService lclOperationalCalculationService,
            ProformaAttachmentService attachmentService

    ) {
        this.typedLclProformaService = typedLclProformaService;
        this.securityUserService = securityUserService;
        this.typedLclPdfService = typedLclPdfService;
        this.lclOperationalCalculationService = lclOperationalCalculationService;
        this.attachmentService = attachmentService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TypedLclProformaResponse>> findAll() {
        return ResponseEntity.ok(typedLclProformaService.findAll());
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping
    public ResponseEntity<TypedLclProformaResponse> create(
            @RequestBody CreateTypedLclProformaRequest request
    ) {
        return ResponseEntity.ok(typedLclProformaService.create(request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TypedLclProformaDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(typedLclProformaService.getById(id));
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PutMapping("/{id}/recalculate")
    public ResponseEntity<TypedLclProformaDetailResponse> recalculate(
            @PathVariable UUID id,
            @RequestBody RecalculateTypedLclProformaRequest request
    ) {
        return ResponseEntity.ok(typedLclProformaService.recalculate(id, request));
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping("/{id}/submit-review")
    public ResponseEntity<TypedLclProformaDetailResponse> submitForReview(
            @PathVariable UUID id,
            @RequestBody(required = false) WorkflowActionRequest request
    ) {
        String actor = request == null ? "system" : request.getActor();
        return ResponseEntity.ok(typedLclProformaService.submitForReview(id, actor));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<TypedLclProformaDetailResponse> approve(
            @PathVariable UUID id,
            @RequestBody(required = false) WorkflowActionRequest request
    ) {
        String actor = securityUserService.getCurrentUser();
        String actorRole = securityUserService.getHighestRole();

        return ResponseEntity.ok(typedLclProformaService.approve(id, actor, actorRole));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<TypedLclProformaDetailResponse> reject(
            @PathVariable UUID id,
            @RequestBody(required = false) RejectWorkflowActionRequest request
    ) {
        String actor = securityUserService.getCurrentUser();
        String reason = request == null ? null : request.getReason();

        return ResponseEntity.ok(typedLclProformaService.reject(id, actor, reason));
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PostMapping("/{id}/client-accept")
    public ResponseEntity<TypedLclProformaDetailResponse> clientAccept(
            @PathVariable UUID id,
            @RequestBody(required = false) WorkflowActionRequest request
    ) {
        String actor = securityUserService.getCurrentUser();
        return ResponseEntity.ok(typedLclProformaService.clientAccept(id, actor));
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PostMapping("/{id}/client-reject")
    public ResponseEntity<TypedLclProformaDetailResponse> clientReject(
            @PathVariable UUID id,
            @RequestBody RejectWorkflowActionRequest request
    ) {
        String actor = securityUserService.getCurrentUser();
        String reason = request == null ? null : request.getReason();

        return ResponseEntity.ok(typedLclProformaService.clientReject(id, actor, reason));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        TypedLclProformaDetailResponse detail = typedLclProformaService.getById(id);

        List<ProformaAttachment> attachments =
                attachmentService.findByProforma(id);

        byte[] pdf = typedLclPdfService.generate(detail, attachments);

        String filename = "proforma-lcl-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping("/operational/calculate")
    public ResponseEntity<LclOperationalCalculationResponse> calculateOperational(
            @RequestBody LclOperationalCalculationRequest request
    ) {
        return ResponseEntity.ok(lclOperationalCalculationService.calculate(request));
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PostMapping("/operational")
    public ResponseEntity<TypedLclProformaDetailResponse> createOperational(
            @RequestBody LclOperationalCalculationRequest request
    ) {
        return ResponseEntity.ok(
                typedLclProformaService.createFromOperational(request)
        );
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN','OWNER')")
    @PutMapping("/{id}/operational")
    public ResponseEntity<TypedLclProformaDetailResponse> updateOperational(
            @PathVariable UUID id,
            @RequestBody LclOperationalCalculationRequest request
    ) {
        return ResponseEntity.ok(
                typedLclProformaService.updateFromOperational(
                        id,
                        request
                )
        );
    }

}