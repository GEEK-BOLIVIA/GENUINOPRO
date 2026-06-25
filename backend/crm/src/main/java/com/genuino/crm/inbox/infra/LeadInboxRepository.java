package com.genuino.crm.inbox.infra;

import com.genuino.crm.inbox.domain.LeadInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface LeadInboxRepository extends JpaRepository<LeadInbox, String> {

    Optional<LeadInbox> findFirstByPhoneOrderByReceivedAtDesc(String phone);

    List<LeadInbox> findByPhoneOrderByReceivedAtDesc(String phone);
    List<LeadInbox> findByAssignedSellerIdOrderByReceivedAtDesc(String assignedSellerId);

    @Query(value = """
            SELECT *
            FROM lead_inbox
            WHERE regexp_replace(coalesce(phone, ''), '[^0-9]', '', 'g') =
                  regexp_replace(coalesce(:phone, ''), '[^0-9]', '', 'g')
            ORDER BY coalesce(received_at, created_at) DESC
            """, nativeQuery = true)
    List<LeadInbox> findRelatedByNormalizedPhoneOrderByReceivedAtDesc(@Param("phone") String phone);

    Optional<LeadInbox> findByExternalConversationId(String externalConversationId);
}