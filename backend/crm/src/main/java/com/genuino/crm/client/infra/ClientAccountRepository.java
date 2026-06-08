package com.genuino.crm.client.infra;

import com.genuino.crm.client.domain.ClientAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAccountRepository extends JpaRepository<ClientAccount, UUID> {
    Optional<ClientAccount> findByLeadId(String leadId);
    Optional<ClientAccount> findByEmail(String email);
    boolean existsByLeadId(String leadId);
    boolean existsByEmail(String email);
}