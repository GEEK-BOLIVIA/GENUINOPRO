package com.genuino.crm.quoting.air.dto;

import java.math.BigDecimal;

public class AirCalculationResponse {

    // Comercial USD
    private BigDecimal fobUsd;
    private BigDecimal warehouseShippingUsd;
    private BigDecimal bankCommissionUsd;
    private BigDecimal airFreightUsd;
    private BigDecimal subtotalUsd;

    // Liquidación aduanera
    private BigDecimal customsFobUsd;
    private BigDecimal customsFreightUsd;
    private BigDecimal insuranceUsd;
    private BigDecimal taxableBaseUsd;
    private BigDecimal cifBorderBob;

    private BigDecimal gaBob;
    private BigDecimal ivaBob;
    private BigDecimal iceBob;
    private BigDecimal customsTaxesBob;

    // Gastos Bolivia
    private BigDecimal anbFormBob;
    private BigDecimal storageBob;
    private BigDecimal folderBob;
    private BigDecimal courierOperationalBob;

    private BigDecimal nationalTaxesBob;
    private BigDecimal dispatchAgencyCommissionBob;
    private BigDecimal genuinoCommissionBob;

    private BigDecimal totalBoliviaBob;

    // Resumen
    private BigDecimal initialPaymentBob;
    private BigDecimal totalBob;
    private BigDecimal unitPriceBob;

    private String calculationRuleVersion;

    public BigDecimal getFobUsd() { return fobUsd; }
    public void setFobUsd(BigDecimal value) { this.fobUsd = value; }

    public BigDecimal getWarehouseShippingUsd() { return warehouseShippingUsd; }
    public void setWarehouseShippingUsd(BigDecimal value) { this.warehouseShippingUsd = value; }

    public BigDecimal getBankCommissionUsd() { return bankCommissionUsd; }
    public void setBankCommissionUsd(BigDecimal value) { this.bankCommissionUsd = value; }

    public BigDecimal getAirFreightUsd() { return airFreightUsd; }
    public void setAirFreightUsd(BigDecimal value) { this.airFreightUsd = value; }

    public BigDecimal getSubtotalUsd() { return subtotalUsd; }
    public void setSubtotalUsd(BigDecimal value) { this.subtotalUsd = value; }

    public BigDecimal getCustomsFobUsd() { return customsFobUsd; }
    public void setCustomsFobUsd(BigDecimal value) { this.customsFobUsd = value; }

    public BigDecimal getCustomsFreightUsd() { return customsFreightUsd; }
    public void setCustomsFreightUsd(BigDecimal value) { this.customsFreightUsd = value; }

    public BigDecimal getInsuranceUsd() { return insuranceUsd; }
    public void setInsuranceUsd(BigDecimal value) { this.insuranceUsd = value; }

    public BigDecimal getTaxableBaseUsd() { return taxableBaseUsd; }
    public void setTaxableBaseUsd(BigDecimal value) { this.taxableBaseUsd = value; }

    public BigDecimal getCifBorderBob() { return cifBorderBob; }
    public void setCifBorderBob(BigDecimal value) { this.cifBorderBob = value; }

    public BigDecimal getGaBob() { return gaBob; }
    public void setGaBob(BigDecimal value) { this.gaBob = value; }

    public BigDecimal getIvaBob() { return ivaBob; }
    public void setIvaBob(BigDecimal value) { this.ivaBob = value; }

    public BigDecimal getIceBob() { return iceBob; }
    public void setIceBob(BigDecimal value) { this.iceBob = value; }

    public BigDecimal getCustomsTaxesBob() { return customsTaxesBob; }
    public void setCustomsTaxesBob(BigDecimal value) { this.customsTaxesBob = value; }

    public BigDecimal getAnbFormBob() { return anbFormBob; }
    public void setAnbFormBob(BigDecimal value) { this.anbFormBob = value; }

    public BigDecimal getStorageBob() { return storageBob; }
    public void setStorageBob(BigDecimal value) { this.storageBob = value; }

    public BigDecimal getFolderBob() { return folderBob; }
    public void setFolderBob(BigDecimal value) { this.folderBob = value; }

    public BigDecimal getCourierOperationalBob() { return courierOperationalBob; }
    public void setCourierOperationalBob(BigDecimal value) { this.courierOperationalBob = value; }

    public BigDecimal getNationalTaxesBob() { return nationalTaxesBob; }
    public void setNationalTaxesBob(BigDecimal value) { this.nationalTaxesBob = value; }

    public BigDecimal getDispatchAgencyCommissionBob() { return dispatchAgencyCommissionBob; }
    public void setDispatchAgencyCommissionBob(BigDecimal value) { this.dispatchAgencyCommissionBob = value; }

    public BigDecimal getGenuinoCommissionBob() { return genuinoCommissionBob; }
    public void setGenuinoCommissionBob(BigDecimal value) { this.genuinoCommissionBob = value; }

    public BigDecimal getTotalBoliviaBob() { return totalBoliviaBob; }
    public void setTotalBoliviaBob(BigDecimal value) { this.totalBoliviaBob = value; }

    public BigDecimal getInitialPaymentBob() { return initialPaymentBob; }
    public void setInitialPaymentBob(BigDecimal value) { this.initialPaymentBob = value; }

    public BigDecimal getTotalBob() { return totalBob; }
    public void setTotalBob(BigDecimal value) { this.totalBob = value; }

    public BigDecimal getUnitPriceBob() { return unitPriceBob; }
    public void setUnitPriceBob(BigDecimal value) { this.unitPriceBob = value; }

    public String getCalculationRuleVersion() { return calculationRuleVersion; }
    public void setCalculationRuleVersion(String value) { this.calculationRuleVersion = value; }
}