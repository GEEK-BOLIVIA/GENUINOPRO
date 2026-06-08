package com.genuino.crm.activity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead_activity")
public class LeadActivity {

    @Id
    public UUID id;

    public String leadId;

    public String type;

    @Column(columnDefinition = "TEXT")
    public String description;

    public String createdBy;

    public OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}