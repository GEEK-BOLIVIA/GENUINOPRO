package com.genuino.crm.crm;

import com.genuino.crm.crm.domain.Customer;
import com.genuino.crm.crm.dto.CustomerSummaryResponse;
import com.genuino.crm.crm.infra.CustomerRepository;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.domain.Proforma;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerSummaryService {

    private final CustomerRepository customerRepository;
    private final ProformaRepository proformaRepository;
    private final OpportunityRepository opportunityRepository;

    public CustomerSummaryService(
            CustomerRepository customerRepository,
            ProformaRepository proformaRepository,
            OpportunityRepository opportunityRepository
    ) {
        this.customerRepository = customerRepository;
        this.proformaRepository = proformaRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public CustomerSummaryResponse getSummary(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        List<Proforma> proformas = proformaRepository.findByCustomerId(customerId);

        long totalProformas = proformas.size();
        long approvedProformas = proformas.stream()
                .filter(p -> "APPROVED".equals(p.status))
                .count();

        long draftProformas = proformas.stream()
                .filter(p -> "DRAFT".equals(p.status))
                .count();

        long inReviewProformas = proformas.stream()
                .filter(p -> "IN_REVIEW".equals(p.status))
                .count();

        BigDecimal totalQuotedAmount = proformas.stream()
                .map(p -> p.total != null ? p.total : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalApprovedAmount = proformas.stream()
                .filter(p -> "APPROVED".equals(p.status))
                .map(p -> p.total != null ? p.total : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Instant lastProformaAt = proformas.stream()
                .map(p -> p.createdAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Instant lastApprovedProformaAt = proformas.stream()
                .filter(p -> "APPROVED".equals(p.status))
                .map(p -> p.approvedAt != null ? p.approvedAt : p.updatedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Set<String> opportunityIds = proformas.stream()
                .map(p -> p.opportunityId)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        long linkedOpportunities = opportunityIds.size();

        long wonOpportunities = opportunityIds.stream()
                .map(id -> opportunityRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(o -> "WON".equals(o.stage))
                .count();

        return new CustomerSummaryResponse(
                customer.id,
                customer.name,
                customer.status,
                totalProformas,
                approvedProformas,
                draftProformas,
                inReviewProformas,
                totalQuotedAmount,
                totalApprovedAmount,
                lastProformaAt,
                lastApprovedProformaAt,
                linkedOpportunities,
                wonOpportunities
        );
    }
}