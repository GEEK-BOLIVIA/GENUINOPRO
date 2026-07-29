package com.genuino.crm.customerprofile.infra;

import com.genuino.crm.customerprofile.domain.BoliviaCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoliviaCityRepository
        extends JpaRepository<BoliviaCity, String> {

    List<BoliviaCity> findByActiveTrueOrderBySortOrderAscNameAsc();
}