package com.genuino.crm.quoting.air.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AirCalculationRequest {

    private String opportunityId;
    private String customerId;

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

    private BigDecimal genuinoCommissionBob;

    private String commercialTerms;

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

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

    public BigDecimal getAirFreightUsd() { return airFreightUsd; }
    public void setAirFreightUsd(BigDecimal airFreightUsd) { this.airFreightUsd = airFreightUsd; }

    public BigDecimal getGaPercent() { return gaPercent; }
    public void setGaPercent(BigDecimal gaPercent) { this.gaPercent = gaPercent; }

    public BigDecimal getIvaPercent() { return ivaPercent; }
    public void setIvaPercent(BigDecimal ivaPercent) { this.ivaPercent = ivaPercent; }

    public BigDecimal getIcePercent() { return icePercent; }
    public void setIcePercent(BigDecimal icePercent) { this.icePercent = icePercent; }

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

    public BigDecimal getGenuinoCommissionBob() { return genuinoCommissionBob; }
    public void setGenuinoCommissionBob(BigDecimal genuinoCommissionBob) { this.genuinoCommissionBob = genuinoCommissionBob; }

    public String getCommercialTerms() { return commercialTerms; }
    public void setCommercialTerms(String commercialTerms) { this.commercialTerms = commercialTerms; }
}