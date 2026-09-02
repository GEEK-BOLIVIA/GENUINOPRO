package com.genuino.crm.quoting.hbl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "typed_proforma_hbl")
public class TypedHblProforma {

    @Id
    @Column(name = "proforma_id", nullable = false)
    private UUID proformaId;

    private LocalDate issueDate;
    private Integer validityDays;
    private String sellerName;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String productName;
    private Integer quantity;
    private BigDecimal merchandiseValueUsd;
    private BigDecimal warehouseShippingUsd;
    private BigDecimal grossWeightKg;
    private BigDecimal volumeCbm;
    private BigDecimal gaPercent;
    private BigDecimal ivaPercent;
    private BigDecimal icePercent;

    @Column(nullable = false)
    private Boolean sensitiveProduct = false;

    private BigDecimal exchangeRate;
    private BigDecimal taxExchangeRate;
    private String supplierName;
    private String supplierPhone;
    private String paymentMethod;
    private String importerNitType;

    @Column(nullable = false)
    private Boolean customerPaysInUsd = false;

    private BigDecimal fobUsd;
    private BigDecimal bankTransferCommissionUsd;
    private BigDecimal maritimeLandFreightUsd;
    private BigDecimal sensitiveProductSurchargeUsd;
    private BigDecimal subtotalUsd;

    @Column(name = "customs_freight_usd")
    private BigDecimal customsFreightUsd;

    @Column(name = "insurance_usd")
    private BigDecimal insuranceUsd;

    @Column(name = "taxable_base_usd")
    private BigDecimal taxableBaseUsd;

    @Column(name = "cif_border_bob")
    private BigDecimal cifBorderBob;

    @Column(name = "total_bolivia_bob")
    private BigDecimal totalBoliviaBob;

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

    @Column(name = "customs_fob_usd")
    private BigDecimal customsFobUsd;

    public BigDecimal getCustomsFobUsd() {
        return customsFobUsd;
    }

    public void setCustomsFobUsd(
            BigDecimal customsFobUsd
    ) {
        this.customsFobUsd = customsFobUsd;
    }

    @Column(columnDefinition = "text")
    private String commercialTerms;

    public UUID getProformaId() { return proformaId; }
    public void setProformaId(UUID proformaId) { this.proformaId = proformaId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getMerchandiseValueUsd() { return merchandiseValueUsd; }
    public void setMerchandiseValueUsd(BigDecimal merchandiseValueUsd) { this.merchandiseValueUsd = merchandiseValueUsd; }
    public BigDecimal getWarehouseShippingUsd() { return warehouseShippingUsd; }
    public void setWarehouseShippingUsd(BigDecimal warehouseShippingUsd) { this.warehouseShippingUsd = warehouseShippingUsd; }
    public BigDecimal getGrossWeightKg() { return grossWeightKg; }
    public void setGrossWeightKg(BigDecimal grossWeightKg) { this.grossWeightKg = grossWeightKg; }
    public BigDecimal getVolumeCbm() { return volumeCbm; }
    public void setVolumeCbm(BigDecimal volumeCbm) { this.volumeCbm = volumeCbm; }
    public BigDecimal getGaPercent() { return gaPercent; }
    public void setGaPercent(BigDecimal gaPercent) { this.gaPercent = gaPercent; }
    public BigDecimal getIvaPercent() { return ivaPercent; }
    public void setIvaPercent(BigDecimal ivaPercent) { this.ivaPercent = ivaPercent; }
    public BigDecimal getIcePercent() { return icePercent; }
    public void setIcePercent(BigDecimal icePercent) { this.icePercent = icePercent; }
    public Boolean getSensitiveProduct() { return sensitiveProduct; }
    public void setSensitiveProduct(Boolean sensitiveProduct) { this.sensitiveProduct = sensitiveProduct; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public BigDecimal getTaxExchangeRate() { return taxExchangeRate; }
    public void setTaxExchangeRate(BigDecimal taxExchangeRate) { this.taxExchangeRate = taxExchangeRate; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierPhone() { return supplierPhone; }
    public void setSupplierPhone(String supplierPhone) { this.supplierPhone = supplierPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getImporterNitType() { return importerNitType; }
    public void setImporterNitType(String importerNitType) { this.importerNitType = importerNitType; }
    public Boolean getCustomerPaysInUsd() { return customerPaysInUsd; }
    public void setCustomerPaysInUsd(Boolean customerPaysInUsd) { this.customerPaysInUsd = customerPaysInUsd; }
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
    public String getCommercialTerms() { return commercialTerms; }
    public void setCommercialTerms(String commercialTerms) { this.commercialTerms = commercialTerms; }

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

