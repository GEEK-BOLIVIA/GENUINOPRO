package com.genuino.crm.quoting.air.service;

import com.genuino.crm.quoting.air.domain.TypedAirProforma;
import com.genuino.crm.quoting.air.dto.AirCalculationRequest;
import com.genuino.crm.quoting.air.dto.AirCalculationResponse;
import com.genuino.crm.quoting.air.dto.TypedAirProformaDetailResponse;

import com.genuino.crm.quoting.common.domain.TypedProforma;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AirProformaMapper {

    public TypedAirProforma toEntity(
            UUID proformaId,
            AirCalculationRequest request,
            AirCalculationResponse calculation
    ) {

        TypedAirProforma entity =
                new TypedAirProforma();

        entity.setProformaId(
                proformaId
        );

        copyInput(
                request,
                entity
        );

        copyCalculation(
                calculation,
                entity
        );

        return entity;
    }

    public TypedAirProformaDetailResponse toDetail(
            TypedProforma header,
            TypedAirProforma entity
    ) {

        TypedAirProformaDetailResponse response =
                new TypedAirProformaDetailResponse();

        response.setId(
                header.getId()
        );

        response.setType(
                header.getType().name()
        );

        response.setStatus(
                header.getStatus().name()
        );

        response.setCurrency(
                header.getCurrency()
        );

        response.setVersion(
                header.getVersion()
        );

        response.setOpportunityId(
                header.getOpportunityId()
        );

        response.setCustomerId(
                header.getCustomerId()
        );

        response.setNotes(
                header.getNotes()
        );

        response.setCreatedBy(
                header.getCreatedBy()
        );

        response.setCreatedAt(
                header.getCreatedAt()
        );

        response.setRejectionReason(
                header.getRejectionReason()
        );

        response.setInput(
                toInput(
                        header,
                        entity
                )
        );

        response.setCalculation(
                toCalculation(entity)
        );

        return response;
    }

    private void copyInput(
            AirCalculationRequest source,
            TypedAirProforma target
    ) {

        target.setIssueDate(
                source.getIssueDate()
        );

        target.setValidityDays(
                source.getValidityDays()
        );

        target.setSellerName(
                source.getSellerName()
        );

        target.setCustomerName(
                source.getCustomerName()
        );

        target.setCustomerPhone(
                source.getCustomerPhone()
        );

        target.setCustomerAddress(
                source.getCustomerAddress()
        );

        target.setProductName(
                source.getProductName()
        );

        target.setQuantity(
                source.getQuantity()
        );

        target.setMerchandiseValueUsd(
                source.getMerchandiseValueUsd()
        );

        target.setWarehouseShippingUsd(
                source.getWarehouseShippingUsd()
        );

        target.setGrossWeightKg(
                source.getGrossWeightKg()
        );

        target.setAirFreightUsd(
                source.getAirFreightUsd()
        );

        target.setGaPercent(
                source.getGaPercent()
        );

        target.setIvaPercent(
                source.getIvaPercent()
        );

        target.setIcePercent(
                source.getIcePercent()
        );

        target.setExchangeRate(
                source.getExchangeRate()
        );

        target.setTaxExchangeRate(
                source.getTaxExchangeRate()
        );

        target.setSupplierName(
                source.getSupplierName()
        );

        target.setSupplierPhone(
                source.getSupplierPhone()
        );

        target.setPaymentMethod(
                source.getPaymentMethod()
        );

        target.setInputGenuinoCommissionBob(
                source.getGenuinoCommissionBob()
        );

        target.setCommercialTerms(
                source.getCommercialTerms()
        );
    }

    private void copyCalculation(
            AirCalculationResponse source,
            TypedAirProforma target
    ) {

        target.setFobUsd(
                source.getFobUsd()
        );

        target.setCalculatedWarehouseShippingUsd(
                source.getWarehouseShippingUsd()
        );

        target.setBankCommissionUsd(
                source.getBankCommissionUsd()
        );

        target.setCalculatedAirFreightUsd(
                source.getAirFreightUsd()
        );

        target.setSubtotalUsd(
                source.getSubtotalUsd()
        );

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

        target.setAnbFormBob(
                source.getAnbFormBob()
        );

        target.setStorageBob(
                source.getStorageBob()
        );

        target.setFolderBob(
                source.getFolderBob()
        );

        target.setCourierOperationalBob(
                source.getCourierOperationalBob()
        );

        target.setNationalTaxesBob(
                source.getNationalTaxesBob()
        );

        target.setDispatchAgencyCommissionBob(
                source.getDispatchAgencyCommissionBob()
        );

        target.setGenuinoCommissionBob(
                source.getGenuinoCommissionBob()
        );

        target.setTotalBoliviaBob(
                source.getTotalBoliviaBob()
        );

        target.setInitialPaymentBob(
                source.getInitialPaymentBob()
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

    private AirCalculationRequest toInput(
            TypedProforma header,
            TypedAirProforma source
    ) {

        AirCalculationRequest target =
                new AirCalculationRequest();

        target.setOpportunityId(
                header.getOpportunityId()
        );

        target.setCustomerId(
                header.getCustomerId()
        );

        target.setIssueDate(
                source.getIssueDate()
        );

        target.setValidityDays(
                source.getValidityDays()
        );

        target.setSellerName(
                source.getSellerName()
        );

        target.setCustomerName(
                source.getCustomerName()
        );

        target.setCustomerPhone(
                source.getCustomerPhone()
        );

        target.setCustomerAddress(
                source.getCustomerAddress()
        );

        target.setProductName(
                source.getProductName()
        );

        target.setQuantity(
                source.getQuantity()
        );

        target.setMerchandiseValueUsd(
                source.getMerchandiseValueUsd()
        );

        target.setWarehouseShippingUsd(
                source.getWarehouseShippingUsd()
        );

        target.setGrossWeightKg(
                source.getGrossWeightKg()
        );

        target.setAirFreightUsd(
                source.getAirFreightUsd()
        );

        target.setGaPercent(
                source.getGaPercent()
        );

        target.setIvaPercent(
                source.getIvaPercent()
        );

        target.setIcePercent(
                source.getIcePercent()
        );

        target.setExchangeRate(
                source.getExchangeRate()
        );

        target.setTaxExchangeRate(
                source.getTaxExchangeRate()
        );

        target.setSupplierName(
                source.getSupplierName()
        );

        target.setSupplierPhone(
                source.getSupplierPhone()
        );

        target.setPaymentMethod(
                source.getPaymentMethod()
        );

        target.setGenuinoCommissionBob(
                source.getInputGenuinoCommissionBob()
        );

        target.setCommercialTerms(
                source.getCommercialTerms()
        );

        return target;
    }

    private AirCalculationResponse toCalculation(
            TypedAirProforma source
    ) {

        AirCalculationResponse target =
                new AirCalculationResponse();

        target.setFobUsd(
                source.getFobUsd()
        );

        target.setWarehouseShippingUsd(
                source.getCalculatedWarehouseShippingUsd()
        );

        target.setBankCommissionUsd(
                source.getBankCommissionUsd()
        );

        target.setAirFreightUsd(
                source.getCalculatedAirFreightUsd()
        );

        target.setSubtotalUsd(
                source.getSubtotalUsd()
        );

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

        target.setAnbFormBob(
                source.getAnbFormBob()
        );

        target.setStorageBob(
                source.getStorageBob()
        );

        target.setFolderBob(
                source.getFolderBob()
        );

        target.setCourierOperationalBob(
                source.getCourierOperationalBob()
        );

        target.setNationalTaxesBob(
                source.getNationalTaxesBob()
        );

        target.setDispatchAgencyCommissionBob(
                source.getDispatchAgencyCommissionBob()
        );

        target.setGenuinoCommissionBob(
                source.getGenuinoCommissionBob()
        );

        target.setTotalBoliviaBob(
                source.getTotalBoliviaBob()
        );

        target.setInitialPaymentBob(
                source.getInitialPaymentBob()
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