package com.genuino.crm.quoting.hbl.service;

import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.hbl.domain.TypedHblProforma;
import com.genuino.crm.quoting.hbl.dto.HblCalculationRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationResponse;
import com.genuino.crm.quoting.hbl.dto.TypedHblProformaDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class HblProformaMapper {

    public TypedHblProforma toEntity(
            java.util.UUID proformaId,
            HblCalculationRequest request,
            HblCalculationResponse calculation
    ) {
        TypedHblProforma entity = new TypedHblProforma();
        entity.setProformaId(proformaId);
        copyInput(request, entity);
        copyCalculation(calculation, entity);
        return entity;
    }

    public TypedHblProformaDetailResponse toDetail(
            TypedProforma header,
            TypedHblProforma entity
    ) {
        TypedHblProformaDetailResponse response = new TypedHblProformaDetailResponse();
        response.setId(header.getId());
        response.setType(header.getType().name());
        response.setStatus(header.getStatus().name());
        response.setCurrency(header.getCurrency());
        response.setVersion(header.getVersion());
        response.setOpportunityId(header.getOpportunityId());
        response.setCustomerId(header.getCustomerId());
        response.setNotes(header.getNotes());
        response.setCreatedBy(header.getCreatedBy());
        response.setCreatedAt(header.getCreatedAt());

        response.setRejectionReason(
                header.getRejectionReason()
        );

        response.setInput(toInput(header, entity));
        response.setCalculation(toCalculation(entity));
        return response;
    }

    private void copyInput(HblCalculationRequest source, TypedHblProforma target) {
        target.setIssueDate(source.getIssueDate());
        target.setValidityDays(source.getValidityDays());
        target.setSellerName(source.getSellerName());
        target.setCustomerName(source.getCustomerName());
        target.setCustomerPhone(source.getCustomerPhone());
        target.setCustomerAddress(source.getCustomerAddress());
        target.setProductName(source.getProductName());
        target.setQuantity(source.getQuantity());
        target.setMerchandiseValueUsd(source.getMerchandiseValueUsd());

        target.setCustomsFobUsd(source.getCustomsFobUsd());

        target.setWarehouseShippingUsd(source.getWarehouseShippingUsd());
        target.setGrossWeightKg(source.getGrossWeightKg());
        target.setVolumeCbm(source.getVolumeCbm());
        target.setGaPercent(source.getGaPercent());
        target.setIvaPercent(source.getIvaPercent());
        target.setIcePercent(source.getIcePercent());
        target.setSensitiveProduct(Boolean.TRUE.equals(source.getSensitiveProduct()));
        target.setExchangeRate(source.getExchangeRate());
        target.setTaxExchangeRate(source.getTaxExchangeRate());
        target.setSupplierName(source.getSupplierName());
        target.setSupplierPhone(source.getSupplierPhone());
        target.setPaymentMethod(source.getPaymentMethod());
        target.setImporterNitType(source.getImporterNitType());
        target.setCustomerPaysInUsd(Boolean.TRUE.equals(source.getCustomerPaysInUsd()));
        target.setCommercialTerms(source.getCommercialTerms());
        target.setMerchandiseValueUsd(source.getMerchandiseValueUsd());
    }

private void copyCalculation(
        HblCalculationResponse source,
        TypedHblProforma target
) {
    target.setFobUsd(
            source.getFobUsd()
    );

    target.setBankTransferCommissionUsd(
            source.getBankTransferCommissionUsd()
    );

    target.setMaritimeLandFreightUsd(
            source.getMaritimeLandFreightUsd()
    );

    target.setSensitiveProductSurchargeUsd(
            source.getSensitiveProductSurchargeUsd()
    );

    target.setSubtotalUsd(
            source.getSubtotalUsd()
    );

    // Liquidación aduanera
    target.setCustomsFobUsd(
            source.getCustomsFobUsd()
    );

    target.setCustomsFreightUsd(
            source.getCustomsFreightUsd()
    );

    target.setInsuranceUsd(
            source.getInsuranceUsd()
    );

    target.setTaxableBaseUsd(
            source.getTaxableBaseUsd()
    );

    target.setCifBorderBob(
            source.getCifBorderBob()
    );

    target.setGaBob(
            source.getGaBob()
    );

    target.setIvaBob(
            source.getIvaBob()
    );

    target.setIceBob(
            source.getIceBob()
    );

    target.setCustomsTaxesBob(
            source.getCustomsTaxesBob()
    );

    target.setAlboCustomsClearanceBob(
            source.getAlboCustomsClearanceBob()
    );

    target.setGenuinoCommissionBob(
            source.getGenuinoCommissionBob()
    );

    target.setDispatchAgentCommissionBob(
            source.getDispatchAgentCommissionBob()
    );

    target.setExtraNitExpensesBob(
            source.getExtraNitExpensesBob()
    );

    target.setTotalBoliviaBob(
            source.getTotalBoliviaBob()
    );

    target.setTotalBob(
            source.getTotalBob()
    );

    target.setUnitPriceBob(
            source.getUnitPriceBob()
    );

    target.setCalculationRuleVersion(
            source.getCalculationRuleVersion()
    );
}



    private HblCalculationRequest toInput(TypedProforma header, TypedHblProforma source) {
        HblCalculationRequest target = new HblCalculationRequest();
        target.setOpportunityId(header.getOpportunityId());
        target.setCustomerId(header.getCustomerId());
        target.setIssueDate(source.getIssueDate());
        target.setValidityDays(source.getValidityDays());
        target.setSellerName(source.getSellerName());
        target.setCustomerName(source.getCustomerName());
        target.setCustomerPhone(source.getCustomerPhone());
        target.setCustomerAddress(source.getCustomerAddress());
        target.setProductName(source.getProductName());
        target.setQuantity(source.getQuantity());
        target.setMerchandiseValueUsd(source.getMerchandiseValueUsd());

        target.setCustomsFobUsd(source.getCustomsFobUsd());

        target.setWarehouseShippingUsd(source.getWarehouseShippingUsd());
        target.setGrossWeightKg(source.getGrossWeightKg());
        target.setVolumeCbm(source.getVolumeCbm());
        target.setGaPercent(source.getGaPercent());
        target.setIvaPercent(source.getIvaPercent());
        target.setIcePercent(source.getIcePercent());
        target.setSensitiveProduct(source.getSensitiveProduct());
        target.setExchangeRate(source.getExchangeRate());
        target.setTaxExchangeRate(source.getTaxExchangeRate());
        target.setSupplierName(source.getSupplierName());
        target.setSupplierPhone(source.getSupplierPhone());
        target.setPaymentMethod(source.getPaymentMethod());
        target.setImporterNitType(source.getImporterNitType());
        target.setCustomerPaysInUsd(source.getCustomerPaysInUsd());
        target.setCommercialTerms(source.getCommercialTerms());
        return target;
    }

private HblCalculationResponse toCalculation(
        TypedHblProforma source
) {
    HblCalculationResponse target =
            new HblCalculationResponse();

    target.setFobUsd(
            source.getFobUsd()
    );

    target.setBankTransferCommissionUsd(
            source.getBankTransferCommissionUsd()
    );

    target.setMaritimeLandFreightUsd(
            source.getMaritimeLandFreightUsd()
    );

    target.setSensitiveProductSurchargeUsd(
            source.getSensitiveProductSurchargeUsd()
    );

    target.setSubtotalUsd(
            source.getSubtotalUsd()
    );

    // Liquidación aduanera
    target.setCustomsFobUsd(
            source.getCustomsFobUsd()
    );

    target.setCustomsFreightUsd(
            source.getCustomsFreightUsd()
    );

    target.setInsuranceUsd(
            source.getInsuranceUsd()
    );

    target.setTaxableBaseUsd(
            source.getTaxableBaseUsd()
    );

    target.setCifBorderBob(
            source.getCifBorderBob()
    );

    target.setGaBob(
            source.getGaBob()
    );

    target.setIvaBob(
            source.getIvaBob()
    );

    target.setIceBob(
            source.getIceBob()
    );

    target.setCustomsTaxesBob(
            source.getCustomsTaxesBob()
    );

    target.setAlboCustomsClearanceBob(
            source.getAlboCustomsClearanceBob()
    );

    target.setGenuinoCommissionBob(
            source.getGenuinoCommissionBob()
    );

    target.setDispatchAgentCommissionBob(
            source.getDispatchAgentCommissionBob()
    );

    target.setExtraNitExpensesBob(
            source.getExtraNitExpensesBob()
    );

    target.setTotalBoliviaBob(
            source.getTotalBoliviaBob()
    );

    target.setTotalBob(
            source.getTotalBob()
    );

    target.setUnitPriceBob(
            source.getUnitPriceBob()
    );

    target.setCalculationRuleVersion(
            source.getCalculationRuleVersion()
    );

    return target;
}
}