package com.genuino.crm.quoting.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "typed_proforma_calculation_snapshot")
public class TypedProformaCalculationSnapshot {

    @Id
    private UUID id;

    @Column(name = "proforma_id", nullable = false)
    private UUID proformaId;

    @Column(name = "calculation_version", nullable = false)
    private Integer calculationVersion;

    @Column(name = "input_json", nullable = false, columnDefinition = "text")
    private String inputJson;

    @Column(name = "output_json", nullable = false, columnDefinition = "text")
    private String outputJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "calculation_mode", length = 20)
    private String calculationMode;

    @Column(name = "calculation_policy_version", length = 50)
    private String calculationPolicyVersion;

    @Column(name = "parameter_snapshot_json", columnDefinition = "text")
    private String parameterSnapshotJson;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProformaId() {
        return proformaId;
    }

    public void setProformaId(UUID proformaId) {
        this.proformaId = proformaId;
    }

    public Integer getCalculationVersion() {
        return calculationVersion;
    }

    public void setCalculationVersion(Integer calculationVersion) {
        this.calculationVersion = calculationVersion;
    }

    public String getInputJson() {
        return inputJson;
    }

    public void setInputJson(String inputJson) {
        this.inputJson = inputJson;
    }

    public String getOutputJson() {
        return outputJson;
    }

    public void setOutputJson(String outputJson) {
        this.outputJson = outputJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public String getCalculationPolicyVersion() {
        return calculationPolicyVersion;
    }

    public void setCalculationPolicyVersion(
            String calculationPolicyVersion
    ) {
        this.calculationPolicyVersion =
                calculationPolicyVersion;
    }

    public String getParameterSnapshotJson() {
        return parameterSnapshotJson;
    }

    public void setParameterSnapshotJson(
            String parameterSnapshotJson
    ) {
        this.parameterSnapshotJson =
                parameterSnapshotJson;
    }
}