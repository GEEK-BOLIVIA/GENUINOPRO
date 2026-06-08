package com.genuino.crm.quoting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

@Entity
@Table(name = "proforma")
public class Proforma {

    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "opportunity_id")
    public String opportunityId;

    @Column(name = "status")
    public String status;

    @Column(name = "currency")
    public String currency;

    @Column(name = "subtotal")
    public BigDecimal subtotal;

    @Column(name = "discount")
    public BigDecimal discount;

    @Column(name = "total")
    public BigDecimal total;

    @Column(name = "series")
    public String series;

    @Column(name = "year")
    public Integer year;

    @Column(name = "number")
    public Integer number;

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "pdf_s3_key")
    public String pdfS3Key;

    @Column(name = "version")
    public Long version;

    @Column(name = "submitted_at")
    public Instant submittedAt;

    @Column(name = "approved_at")
    public Instant approvedAt;

    @Column(name = "rejected_at")
    public Instant rejectedAt;

    @Column(name = "created_at")
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (status == null) status = "DRAFT";
        if (discount == null) discount = BigDecimal.ZERO;
        if (series == null) series = "A";
        if (year == null) year = now.atZone(ZoneOffset.UTC).getYear();
        if (version == null) version = 0L;
    }
}