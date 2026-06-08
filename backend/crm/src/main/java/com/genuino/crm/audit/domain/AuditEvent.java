package com.genuino.crm.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    @Column(name = "audit_id")
    public UUID auditId;

    public Instant ts;

    public String actorUserId;
    public String actorRole;
    public String ip;
    public String userAgent;
    public String traceId;
    public String requestId;

    public String action;
    public String entityType;
    public String entityId;

    @Column(columnDefinition = "jsonb")
    public String beforeJson;

    @Column(columnDefinition = "jsonb")
    public String afterJson;

    public String reason;
    public String result;
    public String errorCode;
}
