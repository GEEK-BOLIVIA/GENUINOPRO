package com.genuino.crm.quoting.infra;

import com.genuino.crm.quoting.domain.Proforma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProformaRepository extends JpaRepository<Proforma, String> {
    List<Proforma> findByOpportunityId(String opportunityId);
    List<Proforma> findByCustomerId(String customerId);
}