package com.genuino.crm.crm.infra;

import com.genuino.crm.crm.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}