package com.genuino.crm.crm;

import com.genuino.crm.crm.domain.Customer;
import com.genuino.crm.crm.dto.CustomerProfile360Response;
import com.genuino.crm.crm.dto.CustomerSummaryResponse;
import com.genuino.crm.crm.dto.CustomerTimelineResponse;
import com.genuino.crm.crm.infra.CustomerRepository;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.infra.ProformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerProfile360Service {

    private final CustomerRepository customerRepository;
    private final CustomerSummaryService customerSummaryService;
    private final CustomerTimelineService customerTimelineService;
    private final ProformaRepository proformaRepository;
    private final OpportunityRepository opportunityRepository;

    public CustomerProfile360Service(
            CustomerRepository customerRepository,
            CustomerSummaryService customerSummaryService,
            CustomerTimelineService customerTimelineService,
            ProformaRepository proformaRepository,
            OpportunityRepository opportunityRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerSummaryService = customerSummaryService;
        this.customerTimelineService = customerTimelineService;
        this.proformaRepository = proformaRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public CustomerProfile360Response getProfile(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        CustomerSummaryResponse summary = customerSummaryService.getSummary(customerId);
        CustomerTimelineResponse timelineResponse = customerTimelineService.getTimeline(customerId);

        Set<String> opportunityIds = proformaRepository.findByCustomerId(customerId).stream()
                .map(p -> p.opportunityId)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Map<String, Object>> opportunities = new ArrayList<>();
        String currentCommercialStatus = "NO_OPPORTUNITY";

        for (String oppId : opportunityIds) {
            opportunityRepository.findById(oppId).ifPresent(opp -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", opp.id);
                item.put("title", opp.title);
                item.put("stage", opp.stage);
                item.put("ownerUserId", opp.ownerUserId);
                item.put("source", opp.source);
                item.put("createdAt", opp.createdAt);
                item.put("updatedAt", opp.updatedAt);
                opportunities.add(item);
            });
        }

        if (!opportunities.isEmpty()) {
            boolean hasWon = opportunities.stream().anyMatch(o -> "WON".equals(o.get("stage")));
            boolean hasProposal = opportunities.stream().anyMatch(o -> "PROPOSAL".equals(o.get("stage")));
            boolean hasNegotiation = opportunities.stream().anyMatch(o -> "NEGOTIATION".equals(o.get("stage")));
            boolean hasContacted = opportunities.stream().anyMatch(o -> "CONTACTED".equals(o.get("stage")));
            boolean hasLead = opportunities.stream().anyMatch(o -> "LEAD".equals(o.get("stage")));
            boolean hasLost = opportunities.stream().allMatch(o -> "LOST".equals(o.get("stage")));

            if (hasWon) currentCommercialStatus = "WON";
            else if (hasNegotiation) currentCommercialStatus = "NEGOTIATION";
            else if (hasProposal) currentCommercialStatus = "PROPOSAL";
            else if (hasContacted) currentCommercialStatus = "CONTACTED";
            else if (hasLead) currentCommercialStatus = "LEAD";
            else if (hasLost) currentCommercialStatus = "LOST";
            else currentCommercialStatus = "IN_PROGRESS";
        }

        Instant lastActivityAt = timelineResponse.events().stream()
                .map(e -> e.get("timestamp"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(Instant::parse)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new CustomerProfile360Response(
                customer.id,
                customer.name,
                customer.status,
                customer.email,
                customer.phone,
                customer.address,
                summary,
                currentCommercialStatus,
                lastActivityAt,
                opportunities,
                timelineResponse.events()
        );
    }
}