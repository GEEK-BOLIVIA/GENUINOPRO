package com.genuino.crm.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "lead_inbox")
public class LeadInbox {

    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "source")
    public String source;

    @Column(name = "external_conversation_id")
    public String externalConversationId;

    @Column(name = "external_contact_id")
    public String externalContactId;

    @Column(name = "phone")
    public String phone;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "message_preview")
    public String messagePreview;

    @Column(name = "channel")
    public String channel;

    @Column(name = "assigned_seller_id")
    public String assignedSellerId;

    @Column(name = "assignment_rule")
    public String assignmentRule;

    @Column(name = "status")
    public String status;

    @Column(name = "payload_json")
    public String payloadJson;

    @Column(name = "received_at")
    public Instant receivedAt;

    @Column(name = "created_at")
    public Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (receivedAt == null) receivedAt = Instant.now();
        if (status == null) status = "NEW";
    }
}