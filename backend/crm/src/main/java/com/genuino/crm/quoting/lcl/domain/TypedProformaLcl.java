package com.genuino.crm.quoting.lcl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "typed_proforma_lcl")
public class TypedProformaLcl {

    @Id
    @Column(name = "proforma_id", nullable = false)
    private UUID proformaId;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "validity_days")
    private Integer validityDays;

    @Column(name = "seller_name", length = 120)
    private String sellerName;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "customer_address", length = 250)
    private String customerAddress;

    @Column(name = "origin_country", length = 120)
    private String originCountry;

    @Column(name = "origin_city", length = 120)
    private String originCity;

    @Column(name = "destination_country", length = 120)
    private String destinationCountry;

    @Column(name = "destination_city", length = 120)
    private String destinationCity;

    @Column(name = "port_origin", length = 150)
    private String portOrigin;

    @Column(name = "port_destination", length = 150)
    private String portDestination;

    @Column(name = "incoterm", length = 20)
    private String incoterm;

    @Column(name = "cargo_type", length = 100)
    private String cargoType;

    @Column(name = "transit_time", length = 100)
    private String transitTime;

    @Column(name = "carrier_name", length = 150)
    private String carrierName;

    @Column(name = "agent_name", length = 150)
    private String agentName;

    @Column(name = "package_count")
    private Integer packageCount;

    @Column(name = "gross_weight_kg", precision = 14, scale = 3)
    private BigDecimal grossWeightKg;

    @Column(name = "volume_cbm", precision = 14, scale = 3)
    private BigDecimal volumeCbm;

    @Column(name = "exchange_rate", precision = 14, scale = 4)
    private BigDecimal exchangeRate;

    @Column(name = "tax_exchange_rate", precision = 14, scale = 4)
    private BigDecimal taxExchangeRate;

    @Column(name = "calculation_rule_version", length = 50)
    private String calculationRuleVersion;

    @Column(name = "cargo_description", columnDefinition = "text")
    private String cargoDescription;

    @Column(name = "freight_rate", precision = 14, scale = 2)
    private BigDecimal freightRate;

    @Column(name = "origin_charges", precision = 14, scale = 2)
    private BigDecimal originCharges;

    @Column(name = "destination_charges", precision = 14, scale = 2)
    private BigDecimal destinationCharges;

    @Column(name = "handling_charges", precision = 14, scale = 2)
    private BigDecimal handlingCharges;

    @Column(name = "documentation_charges", precision = 14, scale = 2)
    private BigDecimal documentationCharges;

    @Column(name = "customs_charges", precision = 14, scale = 2)
    private BigDecimal customsCharges;

    @Column(name = "insurance_charges", precision = 14, scale = 2)
    private BigDecimal insuranceCharges;

    @Column(name = "other_charges", precision = 14, scale = 2)
    private BigDecimal otherCharges;

    @Column(name = "commission_amount", precision = 14, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "margin_amount", precision = 14, scale = 2)
    private BigDecimal marginAmount;

    @Column(name = "subtotal_costs", precision = 14, scale = 2)
    private BigDecimal subtotalCosts;

    @Column(name = "subtotal_sell", precision = 14, scale = 2)
    private BigDecimal subtotalSell;

    @Column(name = "estimated_profit", precision = 14, scale = 2)
    private BigDecimal estimatedProfit;

    @Column(name = "commercial_terms", columnDefinition = "text")
    private String commercialTerms;

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

    public BigDecimal getEstimatedProfit() {
        return estimatedProfit;
    }

    public void setEstimatedProfit(BigDecimal estimatedProfit) {
        this.estimatedProfit = estimatedProfit;
    }

    public String getCommercialTerms() {
        return commercialTerms;
    }

    public void setCommercialTerms(String commercialTerms) {
        this.commercialTerms = commercialTerms;
    }
}