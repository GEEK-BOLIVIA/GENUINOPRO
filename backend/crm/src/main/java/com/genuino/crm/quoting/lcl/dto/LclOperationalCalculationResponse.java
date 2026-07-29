package com.genuino.crm.quoting.lcl.dto;

import java.math.BigDecimal;
import java.util.List;

public class LclOperationalCalculationResponse {

    private BigDecimal merchandiseValueUsd;

    private BigDecimal bankCommissionUsd;

    private BigDecimal maritimeTransportUsd;

    private BigDecimal subtotalUsd;

    private BigDecimal totalUsd;

    private BigDecimal customsTaxesBs;

    private BigDecimal alboBs;

    private BigDecimal miscellaneousExpensesBs;

    private BigDecimal genuinoCommissionBs;

    private BigDecimal totalBs;

    private BigDecimal estimatedProfitBs;

    private BigDecimal unitPriceBs;

    private BigDecimal firstPaymentUsd;

    private BigDecimal secondPaymentUsd;

    private BigDecimal thirdPaymentBs;

    private List<LclOperationalGeneratedLine> generatedLines;

    private BigDecimal usdConvertedToBs;
    private BigDecimal grandTotalBs;

    private BigDecimal exchangeRate;

    private BigDecimal taxExchangeRate;

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

    public BigDecimal getMerchandiseValueUsd() {
        return merchandiseValueUsd;
    }

    public void setMerchandiseValueUsd(BigDecimal merchandiseValueUsd) {
        this.merchandiseValueUsd = merchandiseValueUsd;
    }

    public BigDecimal getBankCommissionUsd() {
        return bankCommissionUsd;
    }

    public void setBankCommissionUsd(BigDecimal bankCommissionUsd) {
        this.bankCommissionUsd = bankCommissionUsd;
    }

    public BigDecimal getMaritimeTransportUsd() {
        return maritimeTransportUsd;
    }

    public void setMaritimeTransportUsd(BigDecimal maritimeTransportUsd) {
        this.maritimeTransportUsd = maritimeTransportUsd;
    }

    public BigDecimal getSubtotalUsd() {
        return subtotalUsd;
    }

    public void setSubtotalUsd(BigDecimal subtotalUsd) {
        this.subtotalUsd = subtotalUsd;
    }

    public BigDecimal getCustomsTaxesBs() {
        return customsTaxesBs;
    }

    public void setCustomsTaxesBs(BigDecimal customsTaxesBs) {
        this.customsTaxesBs = customsTaxesBs;
    }

    public BigDecimal getAlboBs() {
        return alboBs;
    }

    public void setAlboBs(BigDecimal alboBs) {
        this.alboBs = alboBs;
    }

    public BigDecimal getMiscellaneousExpensesBs() {
        return miscellaneousExpensesBs;
    }

    public void setMiscellaneousExpensesBs(BigDecimal miscellaneousExpensesBs) {
        this.miscellaneousExpensesBs = miscellaneousExpensesBs;
    }

    public BigDecimal getGenuinoCommissionBs() {
        return genuinoCommissionBs;
    }

    public void setGenuinoCommissionBs(BigDecimal genuinoCommissionBs) {
        this.genuinoCommissionBs = genuinoCommissionBs;
    }

    public BigDecimal getTotalBs() {
        return totalBs;
    }

    public void setTotalBs(BigDecimal totalBs) {
        this.totalBs = totalBs;
    }

    public BigDecimal getEstimatedProfitBs() {
        return estimatedProfitBs;
    }

    public void setEstimatedProfitBs(BigDecimal estimatedProfitBs) {
        this.estimatedProfitBs = estimatedProfitBs;
    }

    public BigDecimal getUnitPriceBs() {
        return unitPriceBs;
    }

    public void setUnitPriceBs(BigDecimal unitPriceBs) {
        this.unitPriceBs = unitPriceBs;
    }

    public BigDecimal getFirstPaymentUsd() {
        return firstPaymentUsd;
    }

    public void setFirstPaymentUsd(BigDecimal firstPaymentUsd) {
        this.firstPaymentUsd = firstPaymentUsd;
    }

    public BigDecimal getSecondPaymentUsd() {
        return secondPaymentUsd;
    }

    public void setSecondPaymentUsd(BigDecimal secondPaymentUsd) {
        this.secondPaymentUsd = secondPaymentUsd;
    }

    public BigDecimal getThirdPaymentBs() {
        return thirdPaymentBs;
    }

    public void setThirdPaymentBs(BigDecimal thirdPaymentBs) {
        this.thirdPaymentBs = thirdPaymentBs;
    }

    public List<LclOperationalGeneratedLine> getGeneratedLines() {
        return generatedLines;
    }

    public void setGeneratedLines(List<LclOperationalGeneratedLine> generatedLines) {
        this.generatedLines = generatedLines;
    }

    public BigDecimal getTotalUsd() {
        return totalUsd;
    }

    public void setTotalUsd(BigDecimal totalUsd) {
        this.totalUsd = totalUsd;
    }

    public BigDecimal getUsdConvertedToBs() {
        return usdConvertedToBs;
    }

    public void setUsdConvertedToBs(BigDecimal usdConvertedToBs) {
        this.usdConvertedToBs = usdConvertedToBs;
    }

    public BigDecimal getGrandTotalBs() {
        return grandTotalBs;
    }

    public void setGrandTotalBs(BigDecimal grandTotalBs) {
        this.grandTotalBs = grandTotalBs;
    }
}