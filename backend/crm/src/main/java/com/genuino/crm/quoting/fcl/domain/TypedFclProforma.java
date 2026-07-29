package com.genuino.crm.quoting.fcl.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "typed_fcl_proforma")
public class TypedFclProforma {

    @Id
    private UUID id;

    private String code;
    private String customerId;
    private String opportunityId;

    private String customerName;
    private String customerPhone;

    @Column(name = "customer_address")
    private String customerAddress;

    private String sellerName;

    private String originCity;
    private String destinationCity;
    private String product;

    private String supplierName;
    private String supplierPhone;
    private String originPort;

    private String containerType;
    private Integer containerCount;

    private BigDecimal merchandiseValueUsd;
    private BigDecimal fobUsd;
    private BigDecimal exchangeRate;
    private BigDecimal exchangeRateUsed;

    @Column(name = "tax_exchange_rate")
    private BigDecimal taxExchangeRate;

    private BigDecimal originFreightUsd;
    private BigDecimal maritimeFreightUsd;

    @Column(name = "container_release_usd")
    private BigDecimal containerReleaseUsd;

    private BigDecimal inlandFreightBob;

    private BigDecimal insuranceUsd;
    private BigDecimal insuranceUsdCalculated;
    private BigDecimal cifBob;

    private BigDecimal gaPercent;
    private BigDecimal ivaPercent;
    private BigDecimal icePercent;

    private BigDecimal gaBob;
    private BigDecimal ivaBob;
    private BigDecimal iceBob;
    private BigDecimal customsTaxesBob;

    private String paymentMethod;
    private BigDecimal bankTransferCommissionUsd;

    private String importerNitType;
    private BigDecimal totalWeightTn;
    private Integer fobPaymentCount;
    private Boolean customerPaysInUsd;
    private Boolean customerPaysSupplier;
    private BigDecimal extraNitExpensesBob;

    @Column(name = "miscellaneous_expenses_bob")
    private BigDecimal miscellaneousExpensesBob;

    private BigDecimal alboBob;
    private BigDecimal adaBob;
    private BigDecimal commissionUsd;
    private BigDecimal genuinoCommissionBob;
    private BigDecimal dispatchAgentCommissionBob;

    private BigDecimal subtotalUsd;
    private BigDecimal subtotalBob;
    private BigDecimal totalUsdToStartOrder;
    private BigDecimal totalBob;
    private BigDecimal totalOperationBob;

    @Column(name = "calculation_rule_version")
    private String calculationRuleVersion;

    private String currency;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (currency == null) currency = "BOB";
        if (status == null) status = "DRAFT";
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getCustomerId() { return customerId; }
    public String getOpportunityId() { return opportunityId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getSellerName() { return sellerName; }
    public String getOriginCity() { return originCity; }
    public String getDestinationCity() { return destinationCity; }
    public String getProduct() { return product; }
    public String getSupplierName() { return supplierName; }
    public String getSupplierPhone() { return supplierPhone; }
    public String getOriginPort() { return originPort; }
    public String getContainerType() { return containerType; }
    public Integer getContainerCount() { return containerCount; }
    public BigDecimal getMerchandiseValueUsd() { return merchandiseValueUsd; }
    public BigDecimal getFobUsd() { return fobUsd; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public BigDecimal getExchangeRateUsed() { return exchangeRateUsed; }
    public BigDecimal getOriginFreightUsd() { return originFreightUsd; }
    public BigDecimal getMaritimeFreightUsd() { return maritimeFreightUsd; }
    public BigDecimal getInlandFreightBob() { return inlandFreightBob; }
    public BigDecimal getInsuranceUsd() { return insuranceUsd; }
    public BigDecimal getInsuranceUsdCalculated() { return insuranceUsdCalculated; }
    public BigDecimal getCifBob() { return cifBob; }
    public BigDecimal getGaPercent() { return gaPercent; }
    public BigDecimal getIvaPercent() { return ivaPercent; }
    public BigDecimal getIcePercent() { return icePercent; }
    public BigDecimal getGaBob() { return gaBob; }
    public BigDecimal getIvaBob() { return ivaBob; }
    public BigDecimal getIceBob() { return iceBob; }
    public BigDecimal getCustomsTaxesBob() { return customsTaxesBob; }
    public String getPaymentMethod() { return paymentMethod; }
    public BigDecimal getBankTransferCommissionUsd() { return bankTransferCommissionUsd; }
    public String getImporterNitType() { return importerNitType; }
    public BigDecimal getTotalWeightTn() { return totalWeightTn; }
    public Integer getFobPaymentCount() { return fobPaymentCount; }
    public Boolean getCustomerPaysInUsd() { return customerPaysInUsd; }
    public Boolean getCustomerPaysSupplier() { return customerPaysSupplier; }
    public BigDecimal getExtraNitExpensesBob() { return extraNitExpensesBob; }
    public BigDecimal getAlboBob() { return alboBob; }
    public BigDecimal getAdaBob() { return adaBob; }
    public BigDecimal getCommissionUsd() { return commissionUsd; }
    public BigDecimal getGenuinoCommissionBob() { return genuinoCommissionBob; }
    public BigDecimal getDispatchAgentCommissionBob() { return dispatchAgentCommissionBob; }
    public BigDecimal getSubtotalUsd() { return subtotalUsd; }
    public BigDecimal getSubtotalBob() { return subtotalBob; }
    public BigDecimal getTotalUsdToStartOrder() { return totalUsdToStartOrder; }
    public BigDecimal getTotalBob() { return totalBob; }
    public BigDecimal getTotalOperationBob() { return totalOperationBob; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public BigDecimal getTaxExchangeRate() {
        return taxExchangeRate;
    }

    public BigDecimal getContainerReleaseUsd() {
        return containerReleaseUsd;
    }

    public BigDecimal getMiscellaneousExpensesBob() {
        return miscellaneousExpensesBob;
    }

    public String getCalculationRuleVersion() {
        return calculationRuleVersion;
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }

    public void setCode(String code) { this.code = code; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setOpportunityId(String opportunityId) {this.opportunityId = opportunityId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public void setProduct(String product) { this.product = product; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setSupplierPhone(String supplierPhone) { this.supplierPhone = supplierPhone; }
    public void setOriginPort(String originPort) { this.originPort = originPort; }
    public void setContainerType(String containerType) { this.containerType = containerType; }
    public void setContainerCount(Integer containerCount) { this.containerCount = containerCount; }
    public void setMerchandiseValueUsd(BigDecimal merchandiseValueUsd) { this.merchandiseValueUsd = merchandiseValueUsd; }
    public void setFobUsd(BigDecimal fobUsd) { this.fobUsd = fobUsd; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public void setExchangeRateUsed(BigDecimal exchangeRateUsed) { this.exchangeRateUsed = exchangeRateUsed; }
    public void setOriginFreightUsd(BigDecimal originFreightUsd) { this.originFreightUsd = originFreightUsd; }
    public void setMaritimeFreightUsd(BigDecimal maritimeFreightUsd) { this.maritimeFreightUsd = maritimeFreightUsd; }
    public void setInlandFreightBob(BigDecimal inlandFreightBob) { this.inlandFreightBob = inlandFreightBob; }
    public void setInsuranceUsd(BigDecimal insuranceUsd) { this.insuranceUsd = insuranceUsd; }
    public void setInsuranceUsdCalculated(BigDecimal insuranceUsdCalculated) { this.insuranceUsdCalculated = insuranceUsdCalculated; }
    public void setCifBob(BigDecimal cifBob) { this.cifBob = cifBob; }
    public void setGaPercent(BigDecimal gaPercent) { this.gaPercent = gaPercent; }
    public void setIvaPercent(BigDecimal ivaPercent) { this.ivaPercent = ivaPercent; }
    public void setIcePercent(BigDecimal icePercent) { this.icePercent = icePercent; }
    public void setGaBob(BigDecimal gaBob) { this.gaBob = gaBob; }
    public void setIvaBob(BigDecimal ivaBob) { this.ivaBob = ivaBob; }
    public void setIceBob(BigDecimal iceBob) { this.iceBob = iceBob; }
    public void setCustomsTaxesBob(BigDecimal customsTaxesBob) { this.customsTaxesBob = customsTaxesBob; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setBankTransferCommissionUsd(BigDecimal bankTransferCommissionUsd) { this.bankTransferCommissionUsd = bankTransferCommissionUsd; }
    public void setImporterNitType(String importerNitType) { this.importerNitType = importerNitType; }
    public void setTotalWeightTn(BigDecimal totalWeightTn) {
        this.totalWeightTn = totalWeightTn;
    }

    public void setFobPaymentCount(Integer fobPaymentCount) {
        this.fobPaymentCount = fobPaymentCount;
    }

    public void setCustomerPaysInUsd(Boolean customerPaysInUsd) {
        this.customerPaysInUsd = customerPaysInUsd;
    }

    public void setCustomerPaysSupplier(Boolean customerPaysSupplier) {
        this.customerPaysSupplier = customerPaysSupplier;
    }        
    public void setExtraNitExpensesBob(BigDecimal extraNitExpensesBob) { this.extraNitExpensesBob = extraNitExpensesBob; }
    public void setAlboBob(BigDecimal alboBob) { this.alboBob = alboBob; }
    public void setAdaBob(BigDecimal adaBob) { this.adaBob = adaBob; }
    public void setCommissionUsd(BigDecimal commissionUsd) { this.commissionUsd = commissionUsd; }
    public void setGenuinoCommissionBob(BigDecimal genuinoCommissionBob) { this.genuinoCommissionBob = genuinoCommissionBob; }
    public void setDispatchAgentCommissionBob(BigDecimal dispatchAgentCommissionBob) { this.dispatchAgentCommissionBob = dispatchAgentCommissionBob; }
    public void setSubtotalUsd(BigDecimal subtotalUsd) { this.subtotalUsd = subtotalUsd; }
    public void setSubtotalBob(BigDecimal subtotalBob) { this.subtotalBob = subtotalBob; }
    public void setTotalUsdToStartOrder(BigDecimal totalUsdToStartOrder) { this.totalUsdToStartOrder = totalUsdToStartOrder; }
    public void setTotalBob(BigDecimal totalBob) { this.totalBob = totalBob; }
    public void setTotalOperationBob(BigDecimal totalOperationBob) { this.totalOperationBob = totalOperationBob; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setTaxExchangeRate(BigDecimal taxExchangeRate) {
        this.taxExchangeRate = taxExchangeRate;
    }

    public void setContainerReleaseUsd(BigDecimal containerReleaseUsd) {
        this.containerReleaseUsd = containerReleaseUsd;
    }

    public void setMiscellaneousExpensesBob(
            BigDecimal miscellaneousExpensesBob
    ) {
        this.miscellaneousExpensesBob = miscellaneousExpensesBob;
    }

    public void setCalculationRuleVersion(
            String calculationRuleVersion
    ) {
        this.calculationRuleVersion = calculationRuleVersion;
    }
}