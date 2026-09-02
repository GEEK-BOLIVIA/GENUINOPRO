package com.genuino.crm.quoting.fcl.dto;

import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;

public class TypedFclProformaDetailResponse {

    private TypedFclProforma proforma;
    private String rejectionReason;

    public TypedFclProforma getProforma() {
        return proforma;
    }

    public void setProforma(TypedFclProforma proforma) {
        this.proforma = proforma;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}