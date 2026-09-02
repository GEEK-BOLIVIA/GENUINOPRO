package com.genuino.crm.quoting.hbl.dto;

import java.math.BigDecimal;

public class HblCalculationResponse {

    private BigDecimal fobUsd;
    private BigDecimal bankTransferCommissionUsd;
    private BigDecimal maritimeLandFreightUsd;
    private BigDecimal sensitiveProductSurchargeUsd;
    private BigDecimal subtotalUsd;
    private BigDecimal gaBob;
    private BigDecimal ivaBob;
    private BigDecimal iceBob;
    private BigDecimal customsTaxesBob;
    private BigDecimal alboCustomsClearanceBob;
    private BigDecimal genuinoCommissionBob;
    private BigDecimal dispatchAgentCommissionBob;
    private BigDecimal extraNitExpensesBob;
    private BigDecimal totalBob;
    private BigDecimal unitPriceBob;
    private String calculationRuleVersion;

    private BigDecimal customsFobUsd;
    private BigDecimal customsFreightUsd;
    private BigDecimal insuranceUsd;
    private BigDecimal taxableBaseUsd;
    private BigDecimal cifBorderBob;
    private BigDecimal totalBoliviaBob;

    public BigDecimal getFobUsd() { return fobUsd; }
    public void setFobUsd(BigDecimal fobUsd) { this.fobUsd = fobUsd; }
    public BigDecimal getBankTransferCommissionUsd() { return bankTransferCommissionUsd; }
    public void setBankTransferCommissionUsd(BigDecimal value) { this.bankTransferCommissionUsd = value; }
    public BigDecimal getMaritimeLandFreightUsd() { return maritimeLandFreightUsd; }
    public void setMaritimeLandFreightUsd(BigDecimal value) { this.maritimeLandFreightUsd = value; }
    public BigDecimal getSensitiveProductSurchargeUsd() { return sensitiveProductSurchargeUsd; }
    public void setSensitiveProductSurchargeUsd(BigDecimal value) { this.sensitiveProductSurchargeUsd = value; }
    public BigDecimal getSubtotalUsd() { return subtotalUsd; }
    public void setSubtotalUsd(BigDecimal subtotalUsd) { this.subtotalUsd = subtotalUsd; }
    public BigDecimal getGaBob() { return gaBob; }
    public void setGaBob(BigDecimal gaBob) { this.gaBob = gaBob; }
    public BigDecimal getIvaBob() { return ivaBob; }
    public void setIvaBob(BigDecimal ivaBob) { this.ivaBob = ivaBob; }
    public BigDecimal getIceBob() { return iceBob; }
    public void setIceBob(BigDecimal iceBob) { this.iceBob = iceBob; }
    public BigDecimal getCustomsTaxesBob() { return customsTaxesBob; }
    public void setCustomsTaxesBob(BigDecimal value) { this.customsTaxesBob = value; }
    public BigDecimal getAlboCustomsClearanceBob() { return alboCustomsClearanceBob; }
    public void setAlboCustomsClearanceBob(BigDecimal value) { this.alboCustomsClearanceBob = value; }
    public BigDecimal getGenuinoCommissionBob() { return genuinoCommissionBob; }
    public void setGenuinoCommissionBob(BigDecimal value) { this.genuinoCommissionBob = value; }
    public BigDecimal getDispatchAgentCommissionBob() { return dispatchAgentCommissionBob; }
    public void setDispatchAgentCommissionBob(BigDecimal value) { this.dispatchAgentCommissionBob = value; }
    public BigDecimal getExtraNitExpensesBob() { return extraNitExpensesBob; }
    public void setExtraNitExpensesBob(BigDecimal value) { this.extraNitExpensesBob = value; }
    public BigDecimal getTotalBob() { return totalBob; }
    public void setTotalBob(BigDecimal totalBob) { this.totalBob = totalBob; }
    public BigDecimal getUnitPriceBob() { return unitPriceBob; }
    public void setUnitPriceBob(BigDecimal unitPriceBob) { this.unitPriceBob = unitPriceBob; }
    public String getCalculationRuleVersion() { return calculationRuleVersion; }
    public void setCalculationRuleVersion(String value) { this.calculationRuleVersion = value; }

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

    public BigDecimal getTotalBoliviaBob() {
        return totalBoliviaBob;
    }

    public void setTotalBoliviaBob(BigDecimal totalBoliviaBob) {
        this.totalBoliviaBob = totalBoliviaBob;
    }
}