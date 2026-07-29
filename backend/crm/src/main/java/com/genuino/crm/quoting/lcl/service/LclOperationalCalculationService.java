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

@Service
public class LclOperationalCalculationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final ProformaRateService proformaRateService;

    public LclOperationalCalculationService(ProformaRateService proformaRateService) {
        this.proformaRateService = proformaRateService;
    }


    public LclOperationalCalculationResponse calculate(LclOperationalCalculationRequest request) {
        BigDecimal merchandiseValueUsd = money(request.getMerchandiseValueUsd());
        BigDecimal warehouseShippingUsd = money(request.getWarehouseShippingUsd());

        BigDecimal fobBaseUsd = merchandiseValueUsd.add(warehouseShippingUsd);

        BigDecimal bankCommissionUsd = calculateBankCommission(
                fobBaseUsd,
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
            maritimeTransportUsd,
            request.getTaxExchangeRate(),
            request.getGaPercentage(),
            request.getIvaPercentage(),
            request.getIceAmountBs()
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

    private BigDecimal calculateBankCommission(BigDecimal fobBaseUsd, boolean customerPaysUsdCash) {
        if (!customerPaysUsdCash) {
            BigDecimal percent = proformaRateService.findRatePrice(
                    "LCL",
                    "GIRO_PERCENT",
                    fobBaseUsd
            );

            return fobBaseUsd
                    .multiply(percent.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (lte(fobBaseUsd, "5000")) return bd("550");
        if (lte(fobBaseUsd, "9999")) return bd("600");
        if (lte(fobBaseUsd, "11999")) return bd("750");
        if (lte(fobBaseUsd, "14999")) return bd("800");
        if (lte(fobBaseUsd, "17999")) return bd("950");
        if (lte(fobBaseUsd, "19999")) return bd("1050");
        if (lte(fobBaseUsd, "21999")) return bd("1200");
        if (lte(fobBaseUsd, "24500")) return bd("1350");
        if (lte(fobBaseUsd, "29999")) return bd("1700");
        if (lte(fobBaseUsd, "32999")) return bd("1900");
        if (lte(fobBaseUsd, "34999")) return bd("2050");
        if (lte(fobBaseUsd, "39999")) return bd("2200");
        if (lte(fobBaseUsd, "42999")) return bd("2400");
        if (lte(fobBaseUsd, "44999")) return bd("2550");
        if (lte(fobBaseUsd, "50000")) return bd("2700");

        return fobBaseUsd
                .multiply(new BigDecimal("0.055"))
                .setScale(2, RoundingMode.HALF_UP);
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
            BigDecimal maritimeTransportUsd,
            BigDecimal exchangeRate,
            BigDecimal gaPercentage,
            BigDecimal ivaPercentage,
            BigDecimal iceAmountBs
    ) {
        BigDecimal fob = money(merchandiseValueUsd);

        BigDecimal transport = money(maritimeTransportUsd);
       

        BigDecimal gaRate = percent(gaPercentage);
        BigDecimal ivaRate = percent(ivaPercentage);

        BigDecimal insuranceUsd = fob.multiply(new BigDecimal("0.02"));

        BigDecimal transportAduanaUsd = transport.multiply(new BigDecimal("0.76"));

        BigDecimal cifBs = fob
                .add(transportAduanaUsd)
                .add(insuranceUsd)
                .multiply(money(exchangeRate));

        BigDecimal gaBs = cifBs.multiply(gaRate);

        BigDecimal ivaBs = cifBs.add(gaBs).multiply(ivaRate);

        BigDecimal iceBs = money(iceAmountBs);

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