package com.genuino.crm.quoting.hbl.service;

import com.genuino.crm.quoting.hbl.dto.HblCalculationRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

import com.genuino.crm.quoting.common.dto.CustomsLiquidationRequest;
import com.genuino.crm.quoting.common.dto.CustomsLiquidationResponse;
import com.genuino.crm.quoting.common.service.CustomsLiquidationService;

@Service
public class HblCalculationService {

    private static final String RULE_VERSION = "HBL-EXCEL-2026-06-29";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ALIBABA_COMMISSION = new BigDecimal("0.05");
    private static final BigDecimal INSURANCE_RATE = new BigDecimal("0.02");
    private static final BigDecimal DISPATCH_AGENT_COMMISSION_BOB = new BigDecimal("2500");
    private static final BigDecimal EXTRA_NIT_EXPENSE_BOB = new BigDecimal("2800");

    private final CustomsLiquidationService customsLiquidationService;

    private static final List<Tier> GENUINO_COMMISSION_TIERS = tiers(
            "500:700,1000:1500,2000:2000,3000:2800,4000:3500,5000:4500,"
                    + "6000:5500,7000:6500,8000:7500,9000:8500,11000:10000,"
                    + "12000:11500,20000:15000"
    );

    public HblCalculationResponse calculate(HblCalculationRequest request) {
        validate(request);

        BigDecimal merchandiseUsd = money(request.getMerchandiseValueUsd());
        BigDecimal warehouseShippingUsd = money(request.getWarehouseShippingUsd());
        BigDecimal volumeCbm = nonNegative(request.getVolumeCbm());
        BigDecimal grossWeightKg = nonNegative(request.getGrossWeightKg());
        BigDecimal exchangeRate = positive(request.getExchangeRate(), "exchangeRate");
        BigDecimal taxExchangeRate = positive(request.getTaxExchangeRate(), "taxExchangeRate");

        BigDecimal fobUsd = merchandiseUsd.add(warehouseShippingUsd);
        BigDecimal bankCommissionUsd = calculateBankCommission(request, fobUsd);
        BigDecimal maritimeLandFreightUsd = calculateFreight(volumeCbm, grossWeightKg);
        BigDecimal sensitiveSurchargeUsd = Boolean.TRUE.equals(request.getSensitiveProduct())
                ? calculateSensitiveSurcharge(volumeCbm)
                : BigDecimal.ZERO;
        BigDecimal subtotalUsd = fobUsd
                .add(bankCommissionUsd)
                .add(maritimeLandFreightUsd)
                .add(sensitiveSurchargeUsd);

        /*
        * ===============================
        * LIQUIDACIÓN ADUANERA
        * ===============================
        */

        BigDecimal customsFobUsd =
                request.getCustomsFobUsd() != null
                        ? request.getCustomsFobUsd()
                        : merchandiseUsd;

        /*
        * El seguro HBL de la planilla:
        * 2% del FOB real.
        */
        BigDecimal insuranceUsd =
                merchandiseUsd.multiply(
                        INSURANCE_RATE
                );

        CustomsLiquidationRequest customsRequest =
                new CustomsLiquidationRequest();

        customsRequest.setCustomsFobUsd(
                customsFobUsd
        );

        customsRequest.setRealFreightUsd(
                maritimeLandFreightUsd
        );

        customsRequest.setInsuranceUsd(
                insuranceUsd
        );

        customsRequest.setFreightCustomsFactor(
                new BigDecimal("0.10")
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

        BigDecimal gaBob =
                liquidation.getGaBob();

        BigDecimal ivaBob =
                liquidation.getIvaBob();

        BigDecimal iceBob =
                liquidation.getIceBob();

        BigDecimal customsTaxesBob =
                liquidation.getTotalTaxesBob();


        BigDecimal alboBob = calculateAlbo(volumeCbm);
        BigDecimal genuinoCommissionBob = resolveTier(
                merchandiseUsd,
                GENUINO_COMMISSION_TIERS,
                "La comisión Genuino requiere revisión para valores mayores a USD 20.000"
        );
        BigDecimal extraNitExpensesBob = calculateExtraNitExpenses(request.getImporterNitType());

        BigDecimal totalBoliviaBob =
                customsTaxesBob
                        .add(alboBob)
                        .add(genuinoCommissionBob)
                        .add(DISPATCH_AGENT_COMMISSION_BOB)
                        .add(extraNitExpensesBob);

        BigDecimal totalBob =
                subtotalUsd.multiply(exchangeRate)
                        .add(totalBoliviaBob);

        BigDecimal unitPriceBob = totalBob.divide(
                BigDecimal.valueOf(request.getQuantity()),
                8,
                RoundingMode.HALF_UP


        );

        

        HblCalculationResponse response = new HblCalculationResponse();
        response.setFobUsd(scale(fobUsd));
        response.setBankTransferCommissionUsd(scale(bankCommissionUsd));
        response.setMaritimeLandFreightUsd(scale(maritimeLandFreightUsd));
        response.setSensitiveProductSurchargeUsd(scale(sensitiveSurchargeUsd));
        response.setSubtotalUsd(scale(subtotalUsd));

        // Liquidación aduanera


        response.setGaBob(scale(gaBob));
        response.setIvaBob(scale(ivaBob));
        response.setIceBob(scale(iceBob));
        response.setCustomsTaxesBob(scale(customsTaxesBob));
        response.setAlboCustomsClearanceBob(scale(alboBob));
        response.setGenuinoCommissionBob(scale(genuinoCommissionBob));
        response.setDispatchAgentCommissionBob(scale(DISPATCH_AGENT_COMMISSION_BOB));
        response.setExtraNitExpensesBob(scale(extraNitExpensesBob));

        response.setTotalBoliviaBob(
                scale(totalBoliviaBob)
        );
        response.setTotalBob(scale(totalBob));
        response.setUnitPriceBob(scale(unitPriceBob));
        response.setCalculationRuleVersion(RULE_VERSION);

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

        return response;
    }

    private BigDecimal calculateBankCommission(HblCalculationRequest request, BigDecimal fobUsd) {
        if (!"SWIFT".equalsIgnoreCase(request.getPaymentMethod())) {
            return fobUsd.multiply(ALIBABA_COMMISSION);
        }

        boolean paysInUsd = Boolean.TRUE.equals(request.getCustomerPaysInUsd());
        List<Tier> tiers = paysInUsd
                ? tiers(
                        "3000:395,4000:415,5000:435,6000:455,8000:475,10000:500,"
                                + "12999@0.058,15999@0.055,19999@0.053,29999@0.05,"
                                + "39999@0.048,49999@0.045,59999@0.042"
                )
                : tiers(
                        "3000:425,4000:435,5000:455,6000:475,8000:495,10000:520,"
                                + "12999@0.06,15999@0.058,19999@0.055,29999@0.053,"
                                + "39999@0.05,49999@0.048,59999@0.045"
                );

        return resolveTier(
                fobUsd,
                tiers,
                "La comisión SWIFT requiere revisión para valores mayores a USD 59.999"
        );
    }

    private BigDecimal calculateFreight(BigDecimal volumeCbm, BigDecimal grossWeightKg) {
        BigDecimal weightTons = grossWeightKg.divide(new BigDecimal("1000"), 8, RoundingMode.HALF_UP);

        if (weightTons.compareTo(volumeCbm) > 0
                && grossWeightKg.compareTo(volumeCbm.multiply(new BigDecimal("750"))) > 0) {
            return weightTons.multiply(new BigDecimal("450"));
        }

        BigDecimal rate;
        if (volumeCbm.compareTo(BigDecimal.ONE) < 0) {
            rate = new BigDecimal("290");
        } else if (volumeCbm.compareTo(new BigDecimal("5")) < 0) {
            rate = new BigDecimal("280");
        } else if (volumeCbm.compareTo(new BigDecimal("10")) < 0) {
            rate = new BigDecimal("270");
        } else if (volumeCbm.compareTo(new BigDecimal("20")) < 0) {
            rate = new BigDecimal("260");
        } else {
            rate = new BigDecimal("238");
        }
        return volumeCbm.multiply(rate);
    }

    private BigDecimal calculateSensitiveSurcharge(BigDecimal volumeCbm) {
        if (volumeCbm.compareTo(BigDecimal.ONE) <= 0) {
            return new BigDecimal("100");
        }
        if (volumeCbm.compareTo(new BigDecimal("3")) <= 0) {
            return new BigDecimal("200");
        }
        if (volumeCbm.compareTo(new BigDecimal("5")) <= 0) {
            return new BigDecimal("350");
        }
        throw new IllegalArgumentException(
                "El recargo de producto sensible requiere revisión para volúmenes mayores a 5 CBM"
        );
    }

    private BigDecimal calculateAlbo(BigDecimal volumeCbm) {
        List<Tier> tiers = tiers(
                "0.5:700,1:950,2:1100,3:1400,4:1900,5:2700,6:3100,7:3700,"
                        + "8:4100,9:4950,10:5400,11:5500,12:5600,13:5700,"
                        + "14:5800,15:5900,16:6000,17:6100,18:6200,19:6300,"
                        + "20:6400,21:6500"
        );
        return resolveTier(
                volumeCbm,
                tiers,
                "El costo ALBO requiere revisión para volúmenes mayores a 21 CBM"
        );
    }

    private BigDecimal calculateExtraNitExpenses(String importerNitType) {
        if ("LEANDRO".equalsIgnoreCase(importerNitType)
                || "GENUINO".equalsIgnoreCase(importerNitType)) {
            return EXTRA_NIT_EXPENSE_BOB;
        }
        if ("CLIENTE".equalsIgnoreCase(importerNitType)) {
            return BigDecimal.ZERO;
        }
        throw new IllegalArgumentException("importerNitType debe ser CLIENTE, LEANDRO o GENUINO");
    }

    private BigDecimal resolveTier(BigDecimal value, List<Tier> tiers, String outOfRangeMessage) {
        for (Tier tier : tiers) {
            if (value.compareTo(tier.limit()) <= 0) {
                return tier.percentage()
                        ? value.multiply(tier.amount())
                        : tier.amount();
            }
        }
        throw new IllegalArgumentException(outOfRangeMessage);
    }

    private static List<Tier> tiers(String definition) {
        return Arrays.stream(definition.split(","))
                .map(String::trim)
                .map(item -> {
                    boolean percentage = item.contains("@");
                    String[] parts = item.split(percentage ? "@" : ":");
                    return new Tier(
                            new BigDecimal(parts[0]),
                            new BigDecimal(parts[1]),
                            percentage
                    );
                })
                .toList();
    }

    private BigDecimal asRate(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.abs().compareTo(BigDecimal.ONE) <= 0) {
            return value;
        }
        return value.divide(ONE_HUNDRED, 8, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : nonNegative(value);
    }

    private BigDecimal nonNegative(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Los valores numéricos no pueden ser negativos");
        }
        return value;
    }

    private BigDecimal positive(BigDecimal value, String field) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(field + " debe ser mayor a cero");
        }
        return value;
    }

    private void validate(HblCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud HBL es obligatoria");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity debe ser mayor a cero");
        }
        if (request.getVolumeCbm() == null || request.getVolumeCbm().signum() <= 0) {
            throw new IllegalArgumentException("volumeCbm debe ser mayor a cero");
        }
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private record Tier(BigDecimal limit, BigDecimal amount, boolean percentage) {
    }

    public HblCalculationService(
            CustomsLiquidationService customsLiquidationService
    ) {
        this.customsLiquidationService =
                customsLiquidationService;
    }
}