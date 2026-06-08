package com.genuino.crm.opportunity;

import com.genuino.crm.audit.AuditService;
import com.genuino.crm.crm.domain.Customer;
import com.genuino.crm.crm.infra.CustomerRepository;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.OpportunityConvertToCustomerResponse;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OpportunityConversionService {

    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final LeadInboxRepository leadInboxRepository;
    private final ProformaRepository proformaRepository;
    private final AuditService auditService;

    public OpportunityConversionService(
            OpportunityRepository opportunityRepository,
            CustomerRepository customerRepository,
            LeadInboxRepository leadInboxRepository,
            ProformaRepository proformaRepository,
            AuditService auditService
    ) {
        this.opportunityRepository = opportunityRepository;
        this.customerRepository = customerRepository;
        this.leadInboxRepository = leadInboxRepository;
        this.proformaRepository = proformaRepository;
        this.auditService = auditService;
    }

    @Transactional
    public OpportunityConvertToCustomerResponse convertToCustomer(String opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElseThrow();
        Customer customer = ensureCustomerForWonOpportunity(opportunity);

        return new OpportunityConvertToCustomerResponse(
                opportunity.id,
                customer.id,
                customer.name,
                customer.status
        );
    }

    @Transactional
    public Customer ensureCustomerForWonOpportunity(String opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElseThrow();
        return ensureCustomerForWonOpportunity(opportunity);
    }

    @Transactional
    public Customer ensureCustomerForWonOpportunity(Opportunity opportunity) {
        if (!"WON".equals(opportunity.stage)) {
            throw new IllegalStateException("Only WON opportunities can be converted to customer");
        }

        if (opportunity.customerId != null && !opportunity.customerId.isBlank()) {
            Customer existingCustomer = customerRepository.findById(opportunity.customerId).orElseThrow();
            syncProformasToCustomer(opportunity.id, existingCustomer.id);
            return existingCustomer;
        }

        var leadOpt = leadInboxRepository.findById(opportunity.leadInboxId);
        if (leadOpt.isEmpty()) {
            throw new IllegalStateException("Opportunity has no linked lead to create customer from");
        }

        var lead = leadOpt.get();

        Customer customer = new Customer();
        customer.id = "cus_" + UUID.randomUUID();
        customer.name = (lead.fullName != null && !lead.fullName.isBlank())
                ? lead.fullName
                : "Cliente sin nombre";
        customer.taxId = null;
        customer.email = null;
        customer.phone = lead.phone;
        customer.address = null;
        customer.ownerUserId = opportunity.ownerUserId;
        customer.status = "ACTIVE";

        Customer savedCustomer = customerRepository.save(customer);

        Opportunity before = cloneOpportunity(opportunity);

        opportunity.customerId = savedCustomer.id;
        Opportunity savedOpportunity = opportunityRepository.save(opportunity);

        syncProformasToCustomer(savedOpportunity.id, savedCustomer.id);

        auditService.log(
                "CONVERT_TO_CUSTOMER",
                "OPPORTUNITY",
                savedOpportunity.id,
                before,
                savedOpportunity,
                "Conversion automatica/manual de oportunidad ganada a cliente formal",
                "SUCCESS",
                null
        );

        auditService.log(
                "CREATE",
                "CUSTOMER",
                savedCustomer.id,
                null,
                savedCustomer,
                "Creado desde oportunidad ganada",
                "SUCCESS",
                null
        );

        return savedCustomer;
    }

    private void syncProformasToCustomer(String opportunityId, String customerId) {
        var proformas = proformaRepository.findByOpportunityId(opportunityId);

        for (var proforma : proformas) {
            String beforeCustomerId = proforma.customerId;

            proforma.customerId = customerId;
            proformaRepository.save(proforma);

            auditService.log(
                    "SYNC_CUSTOMER",
                    "PROFORMA",
                    proforma.id,
                    beforeCustomerId,
                    proforma.customerId,
                    "Sincronizacion de proforma con cliente formal",
                    "SUCCESS",
                    null
            );
        }
    }

    private Opportunity cloneOpportunity(Opportunity o) {
        Opportunity c = new Opportunity();
        c.id = o.id;
        c.customerId = o.customerId;
        c.leadInboxId = o.leadInboxId;
        c.title = o.title;
        c.stage = o.stage;
        c.source = o.source;
        c.ownerUserId = o.ownerUserId;
        c.notes = o.notes;
        c.createdAt = o.createdAt;
        c.updatedAt = o.updatedAt;
        return c;
    }
}