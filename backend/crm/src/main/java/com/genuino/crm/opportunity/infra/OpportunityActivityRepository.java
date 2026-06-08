package com.genuino.crm.opportunity.infra;

import com.genuino.crm.opportunity.model.OpportunityActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityActivityRepository extends JpaRepository<OpportunityActivity, Long> {

    List<OpportunityActivity> findByOpportunityIdOrderByActivityDateDesc(String opportunityId);
}