package com.genuino.crm.opportunity.infra;

import com.genuino.crm.opportunity.domain.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;


public interface OpportunityRepository extends JpaRepository<Opportunity, String> {

    Optional<Opportunity> findByLeadInboxId(String leadInboxId);

    List<Opportunity> findByOwnerUserIdOrderByUpdatedAtDesc(String ownerUserId);
}