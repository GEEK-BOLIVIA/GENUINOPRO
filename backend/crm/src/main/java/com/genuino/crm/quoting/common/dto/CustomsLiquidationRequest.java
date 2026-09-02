package com.genuino.crm.quoting.common.dto;

import java.math.BigDecimal;

public class CustomsLiquidationRequest {

    private BigDecimal customsFobUsd;
    private BigDecimal realFreightUsd;
    private BigDecimal insuranceUsd;

    private BigDecimal freightCustomsFactor;

    private BigDecimal taxExchangeRate;

    private BigDecimal gaPercent;
    private BigDecimal ivaPercent;
    private BigDecimal icePercent;

    public BigDecimal getCustomsFobUsd() {
        return customsFobUsd;
    }

    public void setCustomsFobUsd(BigDecimal customsFobUsd) {
        this.customsFobUsd = customsFobUsd;
    }

    public BigDecimal getRealFreightUsd() {
        return realFreightUsd;
    }

    public void setRealFreightUsd(BigDecimal realFreightUsd) {
        this.realFreightUsd = realFreightUsd;
    }

    public BigDecimal getInsuranceUsd() {
        return insuranceUsd;
    }

    public void setInsuranceUsd(BigDecimal insuranceUsd) {
        this.insuranceUsd = insuranceUsd;
    }

    public BigDecimal getFreightCustomsFactor() {
        return freightCustomsFactor;
    }

    public void setFreightCustomsFactor(BigDecimal freightCustomsFactor) {
        this.freightCustomsFactor = freightCustomsFactor;
    }

    public BigDecimal getTaxExchangeRate() {
        return taxExchangeRate;
    }

    public void setTaxExchangeRate(BigDecimal taxExchangeRate) {
        this.taxExchangeRate = taxExchangeRate;
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
}