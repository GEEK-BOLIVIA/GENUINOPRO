package com.genuino.crm.crm;

import com.genuino.crm.audit.infra.AuditEventRepository;
import com.genuino.crm.crm.domain.Customer;
import com.genuino.crm.crm.dto.CustomerTimelineResponse;
import com.genuino.crm.crm.infra.CustomerRepository;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerTimelineService {

    private final CustomerRepository customerRepository;
    private final ProformaRepository proformaRepository;
    private final AuditEventRepository auditEventRepository;

    public CustomerTimelineService(
            CustomerRepository customerRepository,
            ProformaRepository proformaRepository,
            AuditEventRepository auditEventRepository
    ) {
        this.customerRepository = customerRepository;
        this.proformaRepository = proformaRepository;
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional(readOnly = true)
    public CustomerTimelineResponse getTimeline(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        List<Map<String, Object>> events = new ArrayList<>();

        Map<String, Object> customerCreated = new LinkedHashMap<>();
        customerCreated.put("type", "CUSTOMER_CREATED");
        customerCreated.put("timestamp", customer.createdAt);
        customerCreated.put("customerId", customer.id);
        customerCreated.put("name", customer.name);
        customerCreated.put("email", customer.email);
        customerCreated.put("phone", customer.phone);
        customerCreated.put("status", customer.status);
        events.add(customerCreated);

        var customerAudit = auditEventRepository
                .findByEntityTypeAndEntityIdOrderByTsAsc("CUSTOMER", customerId);

        for (var audit : customerAudit) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "CUSTOMER_" + audit.action);
            e.put("timestamp", audit.ts);
            e.put("actorUserId", audit.actorUserId);
            e.put("reason", audit.reason);
            e.put("result", audit.result);
            events.add(e);
        }

        var proformas = proformaRepository.findByCustomerId(customerId);

        for (var proforma : proformas) {
            Map<String, Object> created = new LinkedHashMap<>();
            created.put("type", "PROFORMA_CREATED");
            created.put("timestamp", proforma.createdAt);
            created.put("proformaId", proforma.id);
            created.put("status", proforma.status);
            created.put("total", proforma.total);
            created.put("currency", proforma.currency);
            created.put("opportunityId", proforma.opportunityId);
            events.add(created);

            var proformaAudit = auditEventRepository
                    .findByEntityTypeAndEntityIdOrderByTsAsc("PROFORMA", proforma.id);

            for (var audit : proformaAudit) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("type", "PROFORMA_" + audit.action);
                e.put("timestamp", audit.ts);
                e.put("proformaId", proforma.id);
                e.put("actorUserId", audit.actorUserId);
                e.put("reason", audit.reason);
                e.put("result", audit.result);
                events.add(e);
            }
        }

        events.sort(Comparator.comparing(e -> String.valueOf(e.get("timestamp"))));

        return new CustomerTimelineResponse(
                customer.id,
                customer.name,
                customer.status,
                events
        );
    }
}