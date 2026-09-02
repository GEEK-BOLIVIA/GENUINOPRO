package com.genuino.crm.quoting.air.service;

import com.genuino.crm.quoting.air.dto.AirCalculationRequest;
import com.genuino.crm.quoting.air.dto.AirCalculationResponse;

import com.genuino.crm.quoting.common.dto.CustomsLiquidationRequest;
import com.genuino.crm.quoting.common.dto.CustomsLiquidationResponse;
import com.genuino.crm.quoting.common.service.CustomsLiquidationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class AirCalculationService {

    private static final String RULE_VERSION =
            "AIR-EXCEL-2026-07-01";

    private static final BigDecimal BANK_COMMISSION_RATE =
            new BigDecimal("0.05");

    private static final BigDecimal INSURANCE_RATE =
            new BigDecimal("0.02");

    private static final BigDecimal CUSTOMS_FREIGHT_FACTOR =
            new BigDecimal("0.10");

    private static final BigDecimal ANB_FORM_BOB =
            new BigDecimal("105");

    private static final BigDecimal STORAGE_BOB =
            new BigDecimal("40");

    private static final BigDecimal FOLDER_BOB =
            new BigDecimal("100");

    private static final BigDecimal COURIER_OPERATIONAL_BOB =
            new BigDecimal("700");

    private static final BigDecimal DISPATCH_AGENCY_BOB =
            new BigDecimal("1200");

    /*
     * El Excel del 01/07 usa 25% del CIF frontera
     * en la línea denominada "Impuestos Nacionales (13% IVA)".
     * Se mantiene aislado para facilitar su posterior parametrización.
     */
    private static final BigDecimal NATIONAL_TAX_FACTOR =
            new BigDecimal("0.25");

    private final CustomsLiquidationService customsLiquidationService;

    public AirCalculationService(
            CustomsLiquidationService customsLiquidationService
    ) {
        this.customsLiquidationService =
                customsLiquidationService;
    }

    public AirCalculationResponse calculate(
            AirCalculationRequest request
    ) {

        validate(request);

        BigDecimal merchandiseUsd =
                nonNegative(
                        request.getMerchandiseValueUsd()
                );

        BigDecimal warehouseUsd =
                nonNegative(
                        request.getWarehouseShippingUsd()
                );

        BigDecimal weightKg =
                nonNegative(
                        request.getGrossWeightKg()
                );

        BigDecimal airFreightUsd =
                nonNegative(
                        request.getAirFreightUsd()
                );

        BigDecimal exchangeRate =
                positive(
                        request.getExchangeRate(),
                        "exchangeRate"
                );

        BigDecimal taxExchangeRate =
                positive(
                        request.getTaxExchangeRate(),
                        "taxExchangeRate"
                );

        /*
         * ============================
         * CÁLCULO COMERCIAL USD
         * ============================
         */

        BigDecimal fobUsd =
                merchandiseUsd;

        /*
         * Excel:
         * (FOB + Transporte Aéreo) × 5%
         */
        BigDecimal bankCommissionUsd =
                fobUsd
                        .add(airFreightUsd)
                        .multiply(
                                BANK_COMMISSION_RATE
                        );

        BigDecimal subtotalUsd =
                fobUsd
                        .add(warehouseUsd)
                        .add(bankCommissionUsd)
                        .add(airFreightUsd);

        /*
         * ============================
         * LIQUIDACIÓN ADUANERA
         * ============================
         */

        BigDecimal insuranceUsd =
                fobUsd.multiply(
                        INSURANCE_RATE
                );

        CustomsLiquidationRequest customsRequest =
                new CustomsLiquidationRequest();

        customsRequest.setCustomsFobUsd(
                fobUsd
        );

        customsRequest.setRealFreightUsd(
                airFreightUsd
        );

        customsRequest.setInsuranceUsd(
                insuranceUsd
        );

        customsRequest.setFreightCustomsFactor(
                CUSTOMS_FREIGHT_FACTOR
        );

        customsRequest.setTaxExchangeRate(
                taxExchangeRate
        );

        customsRequest.setGaPercent(
                request.getGaPercent()
        );

        customsRequest.setIvaPercent(
                request.getIvaPercent()
        );

        customsRequest.setIcePercent(
                request.getIcePercent()
        );

        CustomsLiquidationResponse liquidation =
                customsLiquidationService.calculate(
                        customsRequest
                );

        /*
         * ============================
         * GASTOS BOLIVIA
         * ============================
         */

        BigDecimal dispatchAgencyBob =
                calculateDispatchAgency(
                        fobUsd,
                        weightKg
                );

        /*
         * Fórmula existente del Excel:
         * CIF frontera × 25%
         */
        BigDecimal nationalTaxesBob =
                liquidation
                        .getCifBorderBob()
                        .multiply(
                                NATIONAL_TAX_FACTOR
                        );

        BigDecimal genuinoCommissionBob =
                nonNegative(
                        request.getGenuinoCommissionBob()
                );

        BigDecimal totalBoliviaBob =
                liquidation
                        .getTotalTaxesBob()
                        .add(ANB_FORM_BOB)
                        .add(STORAGE_BOB)
                        .add(FOLDER_BOB)
                        .add(COURIER_OPERATIONAL_BOB)
                        .add(nationalTaxesBob)
                        .add(dispatchAgencyBob)
                        .add(genuinoCommissionBob);

        /*
         * Pago inicial del Excel:
         * Total USD × T/C comercial
         */
        BigDecimal initialPaymentBob =
                subtotalUsd.multiply(
                        exchangeRate
                );

        BigDecimal totalBob =
                initialPaymentBob
                        .add(totalBoliviaBob);

        BigDecimal unitPriceBob =
                totalBob.divide(
                        BigDecimal.valueOf(
                                request.getQuantity()
                        ),
                        8,
                        RoundingMode.HALF_UP
                );

        AirCalculationResponse response =
                new AirCalculationResponse();

        response.setFobUsd(
                scale(fobUsd)
        );

        response.setWarehouseShippingUsd(
                scale(warehouseUsd)
        );

        response.setBankCommissionUsd(
                scale(bankCommissionUsd)
        );

        response.setAirFreightUsd(
                scale(airFreightUsd)
        );

        response.setSubtotalUsd(
                scale(subtotalUsd)
        );

        response.setCustomsFobUsd(
                liquidation.getCustomsFobUsd()
        );

        response.setCustomsFreightUsd(
                liquidation.getCustomsFreightUsd()
        );

        response.setInsuranceUsd(
                liquidation.getInsuranceUsd()
        );

        response.setTaxableBaseUsd(
                liquidation.getTaxableBaseUsd()
        );

        response.setCifBorderBob(
                liquidation.getCifBorderBob()
        );

        response.setGaBob(
                liquidation.getGaBob()
        );

        response.setIvaBob(
                liquidation.getIvaBob()
        );

        response.setIceBob(
                liquidation.getIceBob()
        );

        response.setCustomsTaxesBob(
                liquidation.getTotalTaxesBob()
        );

        response.setAnbFormBob(
                ANB_FORM_BOB
        );

        response.setStorageBob(
                STORAGE_BOB
        );

        response.setFolderBob(
                FOLDER_BOB
        );

        response.setCourierOperationalBob(
                COURIER_OPERATIONAL_BOB
        );

        response.setNationalTaxesBob(
                scale(nationalTaxesBob)
        );

        response.setDispatchAgencyCommissionBob(
                scale(dispatchAgencyBob)
        );

        response.setGenuinoCommissionBob(
                scale(genuinoCommissionBob)
        );

        response.setTotalBoliviaBob(
                scale(totalBoliviaBob)
        );

        response.setInitialPaymentBob(
                scale(initialPaymentBob)
        );

        response.setTotalBob(
                scale(totalBob)
        );

        response.setUnitPriceBob(
                scale(unitPriceBob)
        );

        response.setCalculationRuleVersion(
                RULE_VERSION
        );

        return response;
    }

    private BigDecimal calculateDispatchAgency(
            BigDecimal fobUsd,
            BigDecimal weightKg
    ) {

        if (fobUsd.compareTo(
                new BigDecimal("1000")
        ) >= 0) {

            return DISPATCH_AGENCY_BOB;
        }

        if (weightKg.compareTo(
                new BigDecimal("40")
        ) >= 0) {

            return DISPATCH_AGENCY_BOB;
        }

        return BigDecimal.ZERO;
    }

    private void validate(
            AirCalculationRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "La solicitud Aéreo es obligatoria"
            );
        }

        if (request.getQuantity() == null
                || request.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "quantity debe ser mayor a cero"
            );
        }

        positive(
                request.getExchangeRate(),
                "exchangeRate"
        );

        positive(
                request.getTaxExchangeRate(),
                "taxExchangeRate"
        );
    }

    private BigDecimal nonNegative(
            BigDecimal value
    ) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Los valores numéricos no pueden ser negativos"
            );
        }

        return value;
    }

    private BigDecimal positive(
            BigDecimal value,
            String field
    ) {

        if (value == null
                || value.signum() <= 0) {

            throw new IllegalArgumentException(
                    field + " debe ser mayor a cero"
            );
        }

        return value;
    }

    private BigDecimal scale(
            BigDecimal value
    ) {

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}