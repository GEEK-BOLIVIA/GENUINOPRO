package com.genuino.crm.quoting.common.api;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.service.ProformaAttachmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proformas/{typedProformaId}/attachments")
public class ProformaAttachmentController {

    private final ProformaAttachmentService service;

    public ProformaAttachmentController(ProformaAttachmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProformaAttachment> list(@PathVariable UUID typedProformaId) {
        return service.findByProforma(typedProformaId);
    }

    @PostMapping
    public ProformaAttachment create(
            @PathVariable UUID typedProformaId,
            @RequestBody ProformaAttachment request
    ) {
        request.setTypedProformaId(typedProformaId);
        return service.create(request);
    }

    @DeleteMapping("/{attachmentId}")
    public void delete(@PathVariable UUID attachmentId) {
        service.delete(attachmentId);
    }
}