package com.genuino.crm.task;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "commercial_task")
public class CommercialTask {

    @Id
    public UUID id;

    public String leadId;
    public String opportunityId;

    public UUID proformaId;

    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    public String status;

    public String priority;

    public String assignedTo;

    public OffsetDateTime dueAt;

    public OffsetDateTime createdAt;

    public OffsetDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (status == null) status = "PENDING";
        if (priority == null) priority = "MEDIA";
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}