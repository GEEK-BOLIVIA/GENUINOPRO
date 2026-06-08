package com.genuino.crm.quoting.common.dto;

import java.math.BigDecimal;

public class ChargeLineResponse {

    private String lineGroup;
    private String code;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;

    public String getLineGroup() {
        return lineGroup;
    }

    public void setLineGroup(String lineGroup) {
        this.lineGroup = lineGroup;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}