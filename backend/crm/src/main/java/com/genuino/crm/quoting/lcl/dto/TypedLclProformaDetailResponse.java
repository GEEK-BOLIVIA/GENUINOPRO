package com.genuino.crm.quoting.lcl.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.lcl.domain.TypedProformaLcl;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaDetailResponse;

import java.util.List;
import com.genuino.crm.quoting.common.dto.ChargeLineResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TypedLclProformaDetailResponse {

    private UUID id;
    private String opportunityId;
    private String customerId;
    private String type;
    private String status;
    private String currency;
    private BigDecimal total;
    private BigDecimal estimatedProfit;
    private String notes;

    private LocalDate issueDate;
    private Integer validityDays;

    private String sellerName;
    private String customerName;
    private String customerPhone;
    private String customerAddress;

    private String originCountry;
    private String originCity;
    private String destinationCountry;
    private String destinationCity;
    private String portOrigin;
    private String portDestination;

    private String incoterm;
    private String cargoType;
    private String transitTime;
    private String carrierName;
    private String agentName;

    private Integer packageCount;
    private BigDecimal grossWeightKg;
    private BigDecimal volumeCbm;

    private BigDecimal exchangeRate;

    private BigDecimal taxExchangeRate;

    private String calculationRuleVersion;
    private String cargoDescription;

    private BigDecimal freightRate;
    private BigDecimal originCharges;
    private BigDecimal destinationCharges;
    private BigDecimal handlingCharges;
    private BigDecimal documentationCharges;
    private BigDecimal customsCharges;
    private BigDecimal insuranceCharges;
    private BigDecimal otherCharges;
    private BigDecimal commissionAmount;
    private BigDecimal marginAmount;

    private BigDecimal subtotalCosts;
    private BigDecimal subtotalSell;
    private String commercialTerms;

    private List<ChargeLineResponse> chargeLines;

    private LocalDateTime createdAt;

    public List<ChargeLineResponse> getChargeLines() {
        return chargeLines;
    }

    public void setChargeLines(List<ChargeLineResponse> chargeLines) {
        this.chargeLines = chargeLines;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getEstimatedProfit() {
        return estimatedProfit;
    }

    public void setEstimatedProfit(BigDecimal estimatedProfit) {
        this.estimatedProfit = estimatedProfit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getPortOrigin() {
        return portOrigin;
    }

    public void setPortOrigin(String portOrigin) {
        this.portOrigin = portOrigin;
    }

    public String getPortDestination() {
        return portDestination;
    }

    public void setPortDestination(String portDestination) {
        this.portDestination = portDestination;
    }

    public String getIncoterm() {
        return incoterm;
    }

    public void setIncoterm(String incoterm) {
        this.incoterm = incoterm;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public String getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(String transitTime) {
        this.transitTime = transitTime;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public BigDecimal getGrossWeightKg() {
        return grossWeightKg;
    }

    public void setGrossWeightKg(BigDecimal grossWeightKg) {
        this.grossWeightKg = grossWeightKg;
    }

    public BigDecimal getVolumeCbm() {
        return volumeCbm;
    }

    public void setVolumeCbm(BigDecimal volumeCbm) {
        this.volumeCbm = volumeCbm;
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

public String getCalculationRuleVersion() {
    return calculationRuleVersion;
}

public void setCalculationRuleVersion(String calculationRuleVersion) {
    this.calculationRuleVersion = calculationRuleVersion;
}

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public BigDecimal getFreightRate() {
        return freightRate;
    }

    public void setFreightRate(BigDecimal freightRate) {
        this.freightRate = freightRate;
    }

    public BigDecimal getOriginCharges() {
        return originCharges;
    }

    public void setOriginCharges(BigDecimal originCharges) {
        this.originCharges = originCharges;
    }

    public BigDecimal getDestinationCharges() {
        return destinationCharges;
    }

    public void setDestinationCharges(BigDecimal destinationCharges) {
        this.destinationCharges = destinationCharges;
    }

    public BigDecimal getHandlingCharges() {
        return handlingCharges;
    }

    public void setHandlingCharges(BigDecimal handlingCharges) {
        this.handlingCharges = handlingCharges;
    }

    public BigDecimal getDocumentationCharges() {
        return documentationCharges;
    }

    public void setDocumentationCharges(BigDecimal documentationCharges) {
        this.documentationCharges = documentationCharges;
    }

    public BigDecimal getCustomsCharges() {
        return customsCharges;
    }

    public void setCustomsCharges(BigDecimal customsCharges) {
        this.customsCharges = customsCharges;
    }

    public BigDecimal getInsuranceCharges() {
        return insuranceCharges;
    }

    public void setInsuranceCharges(BigDecimal insuranceCharges) {
        this.insuranceCharges = insuranceCharges;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getSubtotalCosts() {
        return subtotalCosts;
    }

    public void setSubtotalCosts(BigDecimal subtotalCosts) {
        this.subtotalCosts = subtotalCosts;
    }

    public BigDecimal getSubtotalSell() {
        return subtotalSell;
    }

    public void setSubtotalSell(BigDecimal subtotalSell) {
        this.subtotalSell = subtotalSell;
    }

    public String getCommercialTerms() {
        return commercialTerms;
    }

    public void setCommercialTerms(String commercialTerms) {
        this.commercialTerms = commercialTerms;
    }
}