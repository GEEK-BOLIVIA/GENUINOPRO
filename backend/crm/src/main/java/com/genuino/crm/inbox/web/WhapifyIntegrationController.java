package com.genuino.crm.inbox.web;

import com.genuino.crm.inbox.LeadInboxService;
import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.inbox.dto.WhapifyLeadRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations/whapify")
public class WhapifyIntegrationController {

    private final LeadInboxService service;

    public WhapifyIntegrationController(LeadInboxService service) {
        this.service = service;
    }

    @PostMapping("/leads")
    public LeadInbox receiveLead(@Valid @RequestBody WhapifyLeadRequest req) {
        return service.receiveFromWhapify(req);
    }
}