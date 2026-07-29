package com.genuino.crm.customerprofile.infra;

import com.genuino.crm.customerprofile.domain.LeadCustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadCustomerProfileRepository
        extends JpaRepository<LeadCustomerProfile, String> {
}