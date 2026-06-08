package com.genuino.crm.opportunity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "opportunity")
public class Opportunity {

    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "lead_inbox_id")
    public String leadInboxId;

    @Column(name = "title")
    public String title;

    @Column(name = "stage")
    public String stage;

    @Column(name = "source")
    public String source;

    @Column(name = "owner_user_id")
    public String ownerUserId;

    @Column(name = "notes")
    public String notes;

    @Column(name = "created_at")
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (stage == null) stage = "NUEVO";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
