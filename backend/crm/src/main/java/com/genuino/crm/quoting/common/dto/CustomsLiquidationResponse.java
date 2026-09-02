package com.genuino.crm.quoting.common.dto;

import java.math.BigDecimal;

public class CustomsLiquidationResponse {

    private BigDecimal customsFobUsd;
    private BigDecimal customsFreightUsd;
    private BigDecimal insuranceUsd;

    private BigDecimal taxableBaseUsd;
    private BigDecimal cifBorderBob;

    private BigDecimal gaBob;
    private BigDecimal ivaBob;
    private BigDecimal iceBob;

    private BigDecimal totalTaxesBob;

    public BigDecimal getCustomsFobUsd() {
        return customsFobUsd;
    }

    public void setCustomsFobUsd(BigDecimal customsFobUsd) {
        this.customsFobUsd = customsFobUsd;
    }

    public BigDecimal getCustomsFreightUsd() {
        return customsFreightUsd;
    }

    public void setCustomsFreightUsd(BigDecimal customsFreightUsd) {
        this.customsFreightUsd = customsFreightUsd;
    }

    public BigDecimal getInsuranceUsd() {
        return insuranceUsd;
    }

    public void setInsuranceUsd(BigDecimal insuranceUsd) {
        this.insuranceUsd = insuranceUsd;
    }

    public BigDecimal getTaxableBaseUsd() {
        return taxableBaseUsd;
    }

    public void setTaxableBaseUsd(BigDecimal taxableBaseUsd) {
        this.taxableBaseUsd = taxableBaseUsd;
    }

    public BigDecimal getCifBorderBob() {
        return cifBorderBob;
    }

    public void setCifBorderBob(BigDecimal cifBorderBob) {
        this.cifBorderBob = cifBorderBob;
    }

    public BigDecimal getGaBob() {
        return gaBob;
    }

    public void setGaBob(BigDecimal gaBob) {
        this.gaBob = gaBob;
    }

    public BigDecimal getIvaBob() {
        return ivaBob;
    }

    public void setIvaBob(BigDecimal ivaBob) {
        this.ivaBob = ivaBob;
    }

    public BigDecimal getIceBob() {
        return iceBob;
    }

    public void setIceBob(BigDecimal iceBob) {
        this.iceBob = iceBob;
    }

    public BigDecimal getTotalTaxesBob() {
        return totalTaxesBob;
    }

    public void setTotalTaxesBob(BigDecimal totalTaxesBob) {
        this.totalTaxesBob = totalTaxesBob;
    }
}