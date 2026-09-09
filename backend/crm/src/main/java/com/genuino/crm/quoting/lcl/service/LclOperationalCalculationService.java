package com.genuino.crm.quoting.lcl.service;

import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationRequest;
import com.genuino.crm.quoting.lcl.dto.LclOperationalCalculationResponse;
import com.genuino.crm.quoting.lcl.dto.LclOperationalGeneratedLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.genuino.crm.config.ProformaRateService;

import com.genuino.crm.config.CalculationParameterService;


@Service
public class LclOperationalCalculationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final ProformaRateService proformaRateService;
    private final CalculationParameterService calculationParameterService;

        public LclOperationalCalculationService(
                ProformaRateService proformaRateService,
                CalculationParameterService calculationParameterService
        ) {
        this.proformaRateService = proformaRateService;
        this.calculationParameterService = calculationParameterService;
        }


    public LclOperationalCalculationResponse calculate(LclOperationalCalculationRequest request) {
        BigDecimal merchandiseValueUsd = money(request.getMerchandiseValueUsd());
        BigDecimal warehouseShippingUsd = money(request.getWarehouseShippingUsd());

        BigDecimal fobBaseUsd = merchandiseValueUsd.add(warehouseShippingUsd);

        BigDecimal bankCommissionUsd = calculateBankCommission(
                fobBaseUsd,
                request.getPaymentMethod(),
                Boolean.TRUE.equals(request.getCustomerPaysUsdCash())
        );

        BigDecimal maritimeTransportUsd = calculateMaritimeTransport(
                request.getCbm(),
                request.getWeightKg()
        );

        BigDecimal subtotalUsd = fobBaseUsd
                .add(bankCommissionUsd)
                .add(maritimeTransportUsd);

        BigDecimal customsTaxesBs = calculateCustomsTaxes(
        merchandiseValueUsd,
        request.getCbm(),
        request.getTaxExchangeRate(),
        request.getGaPercentage(),
        request.getIvaPercentage(),
        request.getIcePercentage()
        );

        BigDecimal alboBs = calculateAlbo(request.getCbm());

        BigDecimal miscellaneousExpensesBs = money(request.getMiscellaneousExpensesBs());

        BigDecimal genuinoCommissionBs = calculateGenuinoCommission(merchandiseValueUsd);

        BigDecimal totalBs = customsTaxesBs
                .add(alboBs)
                .add(miscellaneousExpensesBs)
                .add(genuinoCommissionBs)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal usdConvertedToBs = subtotalUsd
                .multiply(money(request.getExchangeRate()))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal grandTotalBs = usdConvertedToBs
                .add(totalBs)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal unitPriceBs = ZERO;

        if (request.getQuantity() != null && request.getQuantity() > 0) {
            unitPriceBs = grandTotalBs
                    .divide(new BigDecimal(request.getQuantity()), 2, RoundingMode.HALF_UP);
        }

        LclOperationalCalculationResponse response = new LclOperationalCalculationResponse();

        response.setMerchandiseValueUsd(fobBaseUsd);
        response.setBankCommissionUsd(bankCommissionUsd);
        response.setMaritimeTransportUsd(maritimeTransportUsd);
        response.setSubtotalUsd(subtotalUsd);
        response.setTotalUsd(subtotalUsd);
        response.setCustomsTaxesBs(customsTaxesBs);
        response.setAlboBs(alboBs);
        response.setMiscellaneousExpensesBs(miscellaneousExpensesBs);
        response.setGenuinoCommissionBs(genuinoCommissionBs);
        response.setEstimatedProfitBs(genuinoCommissionBs);
        response.setTotalBs(totalBs);
        response.setUsdConvertedToBs(usdConvertedToBs);
        response.setGrandTotalBs(grandTotalBs);
        response.setUnitPriceBs(unitPriceBs);

        response.setFirstPaymentUsd(fobBaseUsd.add(bankCommissionUsd));
        response.setSecondPaymentUsd(maritimeTransportUsd);
        response.setThirdPaymentBs(
                customsTaxesBs
                        .add(alboBs)
                        .add(miscellaneousExpensesBs)
                        .add(genuinoCommissionBs)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        response.setGeneratedLines(buildLines(
                fobBaseUsd,
                bankCommissionUsd,
                maritimeTransportUsd,
                customsTaxesBs,
                alboBs,
                miscellaneousExpensesBs,
                genuinoCommissionBs
        ));

        response.setExchangeRate(
                request.getExchangeRate()
        );

        response.setTaxExchangeRate(
                request.getTaxExchangeRate()
        );

        return response;
    }

private BigDecimal calculateBankCommission(
        BigDecimal fobBaseUsd,
        String paymentMethod,
        boolean customerPaysUsdCash
) {
    String method =
            paymentMethod == null
                    ? "ALIBABA"
                    : paymentMethod.trim().toUpperCase();

    if ("ALIBABA".equals(method)) {
        return fobBaseUsd
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    if ("SWIFT".equals(method)
            || "TRANSFERENCIA".equals(method)) {

        if (fobBaseUsd.compareTo(new BigDecimal("50000")) <= 0) {
            return proformaRateService.findRatePrice(
                    "LCL",
                    "COMISION_TRANSFERENCIA",
                    fobBaseUsd
            ).setScale(2, RoundingMode.HALF_UP);
        }

        return fobBaseUsd
                .multiply(new BigDecimal("0.055"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    throw new IllegalArgumentException(
            "Método de pago LCL no soportado: " + paymentMethod
    );
}

private BigDecimal calculateMaritimeTransport(
        BigDecimal cbmValue,
        BigDecimal weightKgValue
) {
    BigDecimal cbm = money(cbmValue);
    BigDecimal weightKg = money(weightKgValue);

    BigDecimal cbmRate =
            proformaRateService.findRatePrice(
                    "LCL",
                    "CBM",
                    cbm
            );

    BigDecimal transportByCbm =
            cbm.multiply(cbmRate);

    BigDecimal maximumWeightByVolumeKg =
            cbm.multiply(
                    new BigDecimal("750")
            );

    if (
        weightKg.compareTo(
                maximumWeightByVolumeKg
        ) > 0
    ) {
        BigDecimal tons =
                weightKg.divide(
                        new BigDecimal("1000"),
                        4,
                        RoundingMode.HALF_UP
                );

        BigDecimal tonRate =
                proformaRateService.findRatePrice(
                        "LCL",
                        "TON",
                        tons
                );

        return tons
                .multiply(tonRate)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    return transportByCbm
            .setScale(
                    2,
                    RoundingMode.HALF_UP
            );
}
    private BigDecimal maritimeRateByCbm(BigDecimal cbm) {
        if (lte(cbm, "0.99")) return bd("250");
        if (lte(cbm, "5")) return bd("220");
        if (lte(cbm, "10")) return bd("210");
        if (lte(cbm, "19.99")) return bd("200");

        return bd("190");
    }

private BigDecimal calculateCustomsTaxes(
        BigDecimal merchandiseValueUsd,
        BigDecimal cbmValue,
        BigDecimal taxExchangeRate,
        BigDecimal gaPercentage,
        BigDecimal ivaPercentage,
        BigDecimal icePercentage
) {
        BigDecimal fob = money(merchandiseValueUsd);
        BigDecimal cbm = money(cbmValue);

        BigDecimal gaRate = percent(gaPercentage);

        BigDecimal ivaRate;

        if (ivaPercentage == null
                || ivaPercentage.compareTo(BigDecimal.ZERO) == 0) {

        ivaRate =
                calculationParameterService.findNumericValue(
                        "LCL",
                        "IVA_PERCENT"
                );

        } else {
        ivaRate = percent(ivaPercentage);
        }

        BigDecimal iceRate =
                percent(icePercentage);

        BigDecimal insuranceRate =
                calculationParameterService.findNumericValue(
                        "LCL",
                        "INSURANCE_PERCENT_DEFAULT"
                );


        BigDecimal insuranceUsd =
                fob.multiply(insuranceRate);

        BigDecimal customsFreightRate =
                calculationParameterService.findNumericValue(
                        "LCL",
                        "CUSTOMS_FREIGHT_USD_PER_CBM"
                );

        BigDecimal customsFreightUsd =
                cbm.multiply(customsFreightRate);

        BigDecimal cifBs = fob
                .add(customsFreightUsd)
                .add(insuranceUsd)
                .multiply(money(taxExchangeRate));

        BigDecimal gaBs =
                cifBs.multiply(gaRate);

        BigDecimal ivaBs =
                cifBs
                        .add(gaBs)
                        .multiply(ivaRate);

        BigDecimal iceBs =
                cifBs
                        .add(gaBs)
                        .multiply(iceRate);

        return gaBs
                .add(ivaBs)
                .add(iceBs)
                .setScale(2, RoundingMode.HALF_UP);
        }

    private BigDecimal calculateAlbo(BigDecimal cbmValue) {
        BigDecimal cbm = money(cbmValue);

        return proformaRateService.findRatePrice(
                "LCL",
                "ALBO",
                cbm
        ).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateGenuinoCommission(BigDecimal merchandiseValueUsd) {
        BigDecimal value = money(merchandiseValueUsd);

        return proformaRateService.findRatePrice(
                "LCL",
                "COMISION_GENUINO",
                value
        ).setScale(2, RoundingMode.HALF_UP);
    }

    private List<LclOperationalGeneratedLine> buildLines(
            BigDecimal fobBaseUsd,
            BigDecimal bankCommissionUsd,
            BigDecimal maritimeTransportUsd,
            BigDecimal customsTaxesBs,
            BigDecimal alboBs,
            BigDecimal miscellaneousExpensesBs,
            BigDecimal genuinoCommissionBs
    ) {
        List<LclOperationalGeneratedLine> lines = new ArrayList<>();

        lines.add(line("FOB", "Valor FOB de la Mercadería", "USD", fobBaseUsd));
        lines.add(line("GIRO", "Comisión Giro Bancario", "USD", bankCommissionUsd));
        lines.add(line("MAR", "Transporte Marítimo", "USD", maritimeTransportUsd));
        lines.add(line("ADU", "Impuestos a la Aduana Nacional", "BOB", customsTaxesBs));
        lines.add(line("ALBO", "Gastos Despacho Aduanero, Albo, DAM, etc.", "BOB", alboBs));
        lines.add(line("VAR", "Gastos varios", "BOB", miscellaneousExpensesBs));
        lines.add(line("COM", "Comisión Genuino Importaciones", "BOB", genuinoCommissionBs));

        return lines;
    }

    private LclOperationalGeneratedLine line(
            String code,
            String description,
            String currency,
            BigDecimal amount
    ) {
        LclOperationalGeneratedLine line = new LclOperationalGeneratedLine();

        line.setCode(code);
        line.setDescription(description);
        line.setCurrency(currency);
        line.setAmount(amount.setScale(2, RoundingMode.HALF_UP));

        return line;
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) return ZERO;

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percent(BigDecimal value) {
        if (value == null) return ZERO;

        if (value.compareTo(BigDecimal.ONE) > 0) {
            return value.divide(ONE_HUNDRED, 6, RoundingMode.HALF_UP);
        }

        return value;
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private boolean lte(BigDecimal value, String limit) {
        return value.compareTo(bd(limit)) <= 0;
    }

    private boolean gt(BigDecimal value, String limit) {
        return value.compareTo(bd(limit)) > 0;
    }

    private boolean between(BigDecimal value, String min, String max) {
        return value.compareTo(bd(min)) >= 0 && value.compareTo(bd(max)) <= 0;
    }
}