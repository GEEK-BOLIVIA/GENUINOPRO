package com.genuino.crm.quoting.common.dto;

import java.math.BigDecimal;

public class UpdateChargeLineRequest {

    private String code;
    private BigDecimal quantity;
    private BigDecimal unitPrice;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}