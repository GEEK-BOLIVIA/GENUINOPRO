package com.genuino.crm.config.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "calculation_parameter")
public class CalculationParameter {

    @Id
    private UUID id;

    private String scope;

    private String code;

    private BigDecimal numericValue;

    @Column(columnDefinition = "TEXT")
    private String textValue;

    private String unit;

    private String version;

    private Boolean active;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (active == null) {
            active = true;
        }

        if (version == null || version.isBlank()) {
            version = "LIQ_2026_09";
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getScope() {
        return scope;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public String getUnit() {
        return unit;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}