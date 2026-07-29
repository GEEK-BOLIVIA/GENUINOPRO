package com.genuino.crm.customerprofile.infra;

import com.genuino.crm.customerprofile.domain.ProformaCustomerSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProformaCustomerSnapshotRepository
        extends JpaRepository<ProformaCustomerSnapshot, UUID> {
}