package com.genuino.crm.quoting.lcl.dto;

import java.math.BigDecimal;

public class LclOperationalCalculationRequest {

    private String customerName;
    private String customerId;
    private String opportunityId;
    private String advisorName;
    private String shippingAddress;
    private String customerPhone;

    private String productName;
    private Integer quantity;

    private BigDecimal merchandiseValueUsd;
    private BigDecimal weightKg;
    private BigDecimal warehouseShippingUsd;

    private BigDecimal gaPercentage;
    private BigDecimal ivaPercentage;
    private BigDecimal miscellaneousExpensesBs;

    private BigDecimal cbm;
    private BigDecimal exchangeRate;

    private String supplierName;
    private String supplierPhone;

    private BigDecimal iceAmountBs;

    private Boolean needsHbl;
    private Boolean customerPaysUsdCash;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getMerchandiseValueUsd() { return merchandiseValueUsd; }
    public void setMerchandiseValueUsd(BigDecimal merchandiseValueUsd) { this.merchandiseValueUsd = merchandiseValueUsd; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getWarehouseShippingUsd() { return warehouseShippingUsd; }
    public void setWarehouseShippingUsd(BigDecimal warehouseShippingUsd) { this.warehouseShippingUsd = warehouseShippingUsd; }

    public BigDecimal getGaPercentage() { return gaPercentage; }
    public void setGaPercentage(BigDecimal gaPercentage) { this.gaPercentage = gaPercentage; }

    public BigDecimal getIvaPercentage() { return ivaPercentage; }
    public void setIvaPercentage(BigDecimal ivaPercentage) { this.ivaPercentage = ivaPercentage; }

    public BigDecimal getMiscellaneousExpensesBs() { return miscellaneousExpensesBs; }
    public void setMiscellaneousExpensesBs(BigDecimal miscellaneousExpensesBs) { this.miscellaneousExpensesBs = miscellaneousExpensesBs; }

    public BigDecimal getCbm() { return cbm; }
    public void setCbm(BigDecimal cbm) { this.cbm = cbm; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierPhone() { return supplierPhone; }
    public void setSupplierPhone(String supplierPhone) { this.supplierPhone = supplierPhone; }

    public BigDecimal getIceAmountBs() { return iceAmountBs; }
    public void setIceAmountBs(BigDecimal iceAmountBs) { this.iceAmountBs = iceAmountBs; }

    public Boolean getNeedsHbl() { return needsHbl; }
    public void setNeedsHbl(Boolean needsHbl) { this.needsHbl = needsHbl; }

    public Boolean getCustomerPaysUsdCash() { return customerPaysUsdCash; }
    public void setCustomerPaysUsdCash(Boolean customerPaysUsdCash) { this.customerPaysUsdCash = customerPaysUsdCash; }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

}
