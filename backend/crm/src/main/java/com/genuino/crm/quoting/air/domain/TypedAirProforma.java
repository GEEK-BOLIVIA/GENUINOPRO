package com.genuino.crm.quoting.air.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "typed_proforma_air")
public class TypedAirProforma {

    @Id
    @Column(name = "proforma_id", nullable = false)
    private UUID proformaId;

    // =========================================================
    // INPUT
    // =========================================================

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
    private BigDecimal airFreightUsd;

    private BigDecimal gaPercent;
    private BigDecimal ivaPercent;
    private BigDecimal icePercent;

    private BigDecimal exchangeRate;
    private BigDecimal taxExchangeRate;

    private String supplierName;
    private String supplierPhone;

    private String paymentMethod;

    private BigDecimal inputGenuinoCommissionBob;

    @Column(columnDefinition = "text")
    private String commercialTerms;

    // =========================================================
    // RESULTADO COMERCIAL USD
    // =========================================================

    private BigDecimal fobUsd;
    private BigDecimal calculatedWarehouseShippingUsd;
    private BigDecimal bankCommissionUsd;
    private BigDecimal calculatedAirFreightUsd;
    private BigDecimal subtotalUsd;

    // =========================================================
    // LIQUIDACIÓN ADUANERA
    // =========================================================

    private BigDecimal customsFobUsd;
    private BigDecimal customsFreightUsd;
    private BigDecimal insuranceUsd;
    private BigDecimal taxableBaseUsd;
    private BigDecimal cifBorderBob;

    private BigDecimal gaBob;
    private BigDecimal ivaBob;
    private BigDecimal iceBob;
    private BigDecimal customsTaxesBob;

    // =========================================================
    // COSTOS BOLIVIA
    // =========================================================

    private BigDecimal anbFormBob;
    private BigDecimal storageBob;
    private BigDecimal folderBob;
    private BigDecimal courierOperationalBob;

    private BigDecimal nationalTaxesBob;
    private BigDecimal dispatchAgencyCommissionBob;
    private BigDecimal genuinoCommissionBob;

    private BigDecimal totalBoliviaBob;

    // =========================================================
    // RESUMEN
    // =========================================================

    private BigDecimal initialPaymentBob;
    private BigDecimal totalBob;
    private BigDecimal unitPriceBob;

    private String calculationRuleVersion;

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public UUID getProformaId() {
        return proformaId;
    }

    public void setProformaId(UUID proformaId) {
        this.proformaId = proformaId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getMerchandiseValueUsd() {
        return merchandiseValueUsd;
    }

    public void setMerchandiseValueUsd(BigDecimal merchandiseValueUsd) {
        this.merchandiseValueUsd = merchandiseValueUsd;
    }

    public BigDecimal getWarehouseShippingUsd() {
        return warehouseShippingUsd;
    }

    public void setWarehouseShippingUsd(BigDecimal warehouseShippingUsd) {
        this.warehouseShippingUsd = warehouseShippingUsd;
    }

    public BigDecimal getGrossWeightKg() {
        return grossWeightKg;
    }

    public void setGrossWeightKg(BigDecimal grossWeightKg) {
        this.grossWeightKg = grossWeightKg;
    }

    public BigDecimal getAirFreightUsd() {
        return airFreightUsd;
    }

    public void setAirFreightUsd(BigDecimal airFreightUsd) {
        this.airFreightUsd = airFreightUsd;
    }

    public BigDecimal getGaPercent() {
        return gaPercent;
    }

    public void setGaPercent(BigDecimal gaPercent) {
        this.gaPercent = gaPercent;
    }

    public BigDecimal getIvaPercent() {
        return ivaPercent;
    }

    public void setIvaPercent(BigDecimal ivaPercent) {
        this.ivaPercent = ivaPercent;
    }

    public BigDecimal getIcePercent() {
        return icePercent;
    }

    public void setIcePercent(BigDecimal icePercent) {
        this.icePercent = icePercent;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getTaxExchangeRate() {
        return taxExchangeRate;
    }

    public void setTaxExchangeRate(BigDecimal taxExchangeRate) {
        this.taxExchangeRate = taxExchangeRate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getInputGenuinoCommissionBob() {
        return inputGenuinoCommissionBob;
    }

    public void setInputGenuinoCommissionBob(BigDecimal inputGenuinoCommissionBob) {
        this.inputGenuinoCommissionBob = inputGenuinoCommissionBob;
    }

    public String getCommercialTerms() {
        return commercialTerms;
    }

    public void setCommercialTerms(String commercialTerms) {
        this.commercialTerms = commercialTerms;
    }

    public BigDecimal getFobUsd() {
        return fobUsd;
    }

    public void setFobUsd(BigDecimal fobUsd) {
        this.fobUsd = fobUsd;
    }

    public BigDecimal getCalculatedWarehouseShippingUsd() {
        return calculatedWarehouseShippingUsd;
    }

    public void setCalculatedWarehouseShippingUsd(BigDecimal calculatedWarehouseShippingUsd) {
        this.calculatedWarehouseShippingUsd = calculatedWarehouseShippingUsd;
    }

    public BigDecimal getBankCommissionUsd() {
        return bankCommissionUsd;
    }

    public void setBankCommissionUsd(BigDecimal bankCommissionUsd) {
        this.bankCommissionUsd = bankCommissionUsd;
    }

    public BigDecimal getCalculatedAirFreightUsd() {
        return calculatedAirFreightUsd;
    }

    public void setCalculatedAirFreightUsd(BigDecimal calculatedAirFreightUsd) {
        this.calculatedAirFreightUsd = calculatedAirFreightUsd;
    }

    public BigDecimal getSubtotalUsd() {
        return subtotalUsd;
    }

    public void setSubtotalUsd(BigDecimal subtotalUsd) {
        this.subtotalUsd = subtotalUsd;
    }

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

    public BigDecimal getCustomsTaxesBob() {
        return customsTaxesBob;
    }

    public void setCustomsTaxesBob(BigDecimal customsTaxesBob) {
        this.customsTaxesBob = customsTaxesBob;
    }

    public BigDecimal getAnbFormBob() {
        return anbFormBob;
    }

    public void setAnbFormBob(BigDecimal anbFormBob) {
        this.anbFormBob = anbFormBob;
    }

    public BigDecimal getStorageBob() {
        return storageBob;
    }

    public void setStorageBob(BigDecimal storageBob) {
        this.storageBob = storageBob;
    }

    public BigDecimal getFolderBob() {
        return folderBob;
    }

    public void setFolderBob(BigDecimal folderBob) {
        this.folderBob = folderBob;
    }

    public BigDecimal getCourierOperationalBob() {
        return courierOperationalBob;
    }

    public void setCourierOperationalBob(BigDecimal courierOperationalBob) {
        this.courierOperationalBob = courierOperationalBob;
    }

    public BigDecimal getNationalTaxesBob() {
        return nationalTaxesBob;
    }

    public void setNationalTaxesBob(BigDecimal nationalTaxesBob) {
        this.nationalTaxesBob = nationalTaxesBob;
    }

    public BigDecimal getDispatchAgencyCommissionBob() {
        return dispatchAgencyCommissionBob;
    }

    public void setDispatchAgencyCommissionBob(BigDecimal dispatchAgencyCommissionBob) {
        this.dispatchAgencyCommissionBob = dispatchAgencyCommissionBob;
    }

    public BigDecimal getGenuinoCommissionBob() {
        return genuinoCommissionBob;
    }

    public void setGenuinoCommissionBob(BigDecimal genuinoCommissionBob) {
        this.genuinoCommissionBob = genuinoCommissionBob;
    }

    public BigDecimal getTotalBoliviaBob() {
        return totalBoliviaBob;
    }

    public void setTotalBoliviaBob(BigDecimal totalBoliviaBob) {
        this.totalBoliviaBob = totalBoliviaBob;
    }

    public BigDecimal getInitialPaymentBob() {
        return initialPaymentBob;
    }

    public void setInitialPaymentBob(BigDecimal initialPaymentBob) {
        this.initialPaymentBob = initialPaymentBob;
    }

    public BigDecimal getTotalBob() {
        return totalBob;
    }

    public void setTotalBob(BigDecimal totalBob) {
        this.totalBob = totalBob;
    }

    public BigDecimal getUnitPriceBob() {
        return unitPriceBob;
    }

    public void setUnitPriceBob(BigDecimal unitPriceBob) {
        this.unitPriceBob = unitPriceBob;
    }

    public String getCalculationRuleVersion() {
        return calculationRuleVersion;
    }

    public void setCalculationRuleVersion(String calculationRuleVersion) {
        this.calculationRuleVersion = calculationRuleVersion;
    }
}