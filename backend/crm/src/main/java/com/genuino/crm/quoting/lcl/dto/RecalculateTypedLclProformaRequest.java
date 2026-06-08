package com.genuino.crm.quoting.lcl.dto;

import com.genuino.crm.quoting.common.dto.UpdateChargeLineRequest;
import java.util.List;

public class RecalculateTypedLclProformaRequest {

    private List<UpdateChargeLineRequest> chargeLines;
    private String updatedBy;

    public List<UpdateChargeLineRequest> getChargeLines() {
        return chargeLines;
    }

    public void setChargeLines(List<UpdateChargeLineRequest> chargeLines) {
        this.chargeLines = chargeLines;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}