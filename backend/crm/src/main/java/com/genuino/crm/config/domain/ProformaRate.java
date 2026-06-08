package com.genuino.crm.config.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proforma_rate")
public class ProformaRate {

    @Id
    private UUID id;

    private String proformaType;
    private String rateType;
    private BigDecimal rangeFrom;
    private BigDecimal rangeTo;
    private BigDecimal price;
    private String currency;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (active == null) active = true;
        if (currency == null) currency = "USD";
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getProformaType() { return proformaType; }
    public String getRateType() { return rateType; }
    public BigDecimal getRangeFrom() { return rangeFrom; }
    public BigDecimal getRangeTo() { return rangeTo; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public Boolean getActive() { return active; }

    public void setId(UUID id) { this.id = id; }
    public void setProformaType(String proformaType) { this.proformaType = proformaType; }
    public void setRateType(String rateType) { this.rateType = rateType; }
    public void setRangeFrom(BigDecimal rangeFrom) { this.rangeFrom = rangeFrom; }
    public void setRangeTo(BigDecimal rangeTo) { this.rangeTo = rangeTo; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setActive(Boolean active) { this.active = active; }


}