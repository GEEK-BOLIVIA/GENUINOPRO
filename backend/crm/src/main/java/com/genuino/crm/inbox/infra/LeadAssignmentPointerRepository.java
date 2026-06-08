package com.genuino.crm.inbox.infra;

import com.genuino.crm.inbox.domain.LeadAssignmentPointer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadAssignmentPointerRepository extends JpaRepository<LeadAssignmentPointer, String> {
}