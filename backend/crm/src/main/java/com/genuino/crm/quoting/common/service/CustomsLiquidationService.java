package com.genuino.crm.quoting.common.service;

import com.genuino.crm.quoting.common.dto.CustomsLiquidationRequest;
import com.genuino.crm.quoting.common.dto.CustomsLiquidationResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class CustomsLiquidationService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    public CustomsLiquidationResponse calculate(
            CustomsLiquidationRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Los parámetros de liquidación son obligatorios"
            );
        }

        BigDecimal customsFobUsd =
                nonNegative(request.getCustomsFobUsd());

        BigDecimal realFreightUsd =
                nonNegative(request.getRealFreightUsd());

        BigDecimal insuranceUsd =
                nonNegative(request.getInsuranceUsd());

        BigDecimal taxExchangeRate =
                positive(
                        request.getTaxExchangeRate(),
                        "taxExchangeRate"
                );

        /*
         * Planilla gubernamental:
         * sólo una parte del flete real se utiliza
         * para efectos aduaneros.
         *
         * Actualmente: 10%
         */
        BigDecimal freightFactor =
                request.getFreightCustomsFactor() != null
                        ? request.getFreightCustomsFactor()
                        : new BigDecimal("0.10");

        BigDecimal customsFreightUsd =
                realFreightUsd.multiply(freightFactor);

        /*
         * BASE IMPONIBLE USD
         *
         * FOB efectos Aduana
         * + transporte efectos Aduana
         * + seguro
         */
        BigDecimal taxableBaseUsd =
                customsFobUsd
                        .add(customsFreightUsd)
                        .add(insuranceUsd);

        /*
         * CIF FRONTERA EN Bs
         */
        BigDecimal cifBorderBob =
                taxableBaseUsd.multiply(
                        taxExchangeRate
                );

        BigDecimal gaBob =
                cifBorderBob.multiply(
                        rate(request.getGaPercent())
                );

        /*
         * Base IVA/ICE:
         * CIF + GA
         */
        BigDecimal ivaIceBase =
                cifBorderBob.add(gaBob);

        BigDecimal ivaBob =
                ivaIceBase.multiply(
                        rate(request.getIvaPercent())
                );

        BigDecimal iceBob =
                ivaIceBase.multiply(
                        rate(request.getIcePercent())
                );

        BigDecimal totalTaxesBob =
                gaBob
                        .add(ivaBob)
                        .add(iceBob);

        CustomsLiquidationResponse response =
                new CustomsLiquidationResponse();

        response.setCustomsFobUsd(
                scale(customsFobUsd)
        );

        response.setCustomsFreightUsd(
                scale(customsFreightUsd)
        );

        response.setInsuranceUsd(
                scale(insuranceUsd)
        );

        response.setTaxableBaseUsd(
                scale(taxableBaseUsd)
        );

        response.setCifBorderBob(
                scale(cifBorderBob)
        );

        response.setGaBob(
                scale(gaBob)
        );

        response.setIvaBob(
                scale(ivaBob)
        );

        response.setIceBob(
                scale(iceBob)
        );

        response.setTotalTaxesBob(
                scale(totalTaxesBob)
        );

        return response;
    }

    private BigDecimal rate(BigDecimal value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value.abs().compareTo(BigDecimal.ONE) <= 0) {
            return value;
        }

        return value.divide(
                ONE_HUNDRED,
                8,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal nonNegative(BigDecimal value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Los valores de liquidación no pueden ser negativos"
            );
        }

        return value;
    }

    private BigDecimal positive(
            BigDecimal value,
            String field
    ) {

        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(
                    field + " debe ser mayor a cero"
            );
        }

        return value;
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}