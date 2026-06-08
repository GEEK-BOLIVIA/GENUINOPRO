package com.genuino.crm.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "lead_assignment_pointer")
public class LeadAssignmentPointer {

    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "last_assigned_seller_id")
    public String lastAssignedSellerId;

    @Column(name = "updated_at")
    public Instant updatedAt;
}