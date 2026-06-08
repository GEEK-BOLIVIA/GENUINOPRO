package com.genuino.crm.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommercialTaskRepository extends JpaRepository<CommercialTask, UUID> {

    List<CommercialTask> findByLeadIdOrderByDueAtAsc(String leadId);

    List<CommercialTask> findByOpportunityIdOrderByDueAtAsc(
            String opportunityId
    );

    List<CommercialTask> findByProformaIdOrderByDueAtAsc(
            UUID proformaId
    );

    List<CommercialTask> findByAssignedToOrderByDueAtAsc(String assignedTo);
}