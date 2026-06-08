package com.genuino.crm.quoting.lcl.service;

import com.genuino.crm.quoting.lcl.dto.CreateTypedLclProformaRequest;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

import com.genuino.crm.quoting.common.domain.TypedProformaChargeLine;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LclCalculationService {

    public BigDecimal calculateFreightCost(CreateTypedLclProformaRequest request) {
        BigDecimal freightRate = defaultIfNull(request.getFreightRate());
        BigDecimal volumeCbm = defaultIfNull(request.getVolumeCbm());
        return freightRate.multiply(volumeCbm);
    }

    public BigDecimal calculateSubtotalCosts(CreateTypedLclProformaRequest request) {
        return calculateFreightCost(request)
                .add(defaultIfNull(request.getOriginCharges()))
                .add(defaultIfNull(request.getDestinationCharges()))
                .add(defaultIfNull(request.getHandlingCharges()))
                .add(defaultIfNull(request.getDocumentationCharges()))
                .add(defaultIfNull(request.getCustomsCharges()))
                .add(defaultIfNull(request.getInsuranceCharges()))
                .add(defaultIfNull(request.getOtherCharges()));
    }

    public BigDecimal calculateSubtotalSell(CreateTypedLclProformaRequest request) {
        return calculateSubtotalCosts(request)
                .add(defaultIfNull(request.getCommissionAmount()))
                .add(defaultIfNull(request.getMarginAmount()));
    }

    public BigDecimal calculateEstimatedProfit(CreateTypedLclProformaRequest request) {
        return calculateSubtotalSell(request).subtract(calculateSubtotalCosts(request));
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public List<TypedProformaChargeLine> buildChargeLines(UUID proformaId, CreateTypedLclProformaRequest request) {
        List<TypedProformaChargeLine> lines = new ArrayList<>();

        lines.add(line(proformaId, "COST", "FRT", "Freight", request.getVolumeCbm(), request.getFreightRate(), false, 1));
        lines.add(line(proformaId, "COST", "ORG", "Origin charges", BigDecimal.ONE, request.getOriginCharges(), true, 2));
        lines.add(line(proformaId, "COST", "DST", "Destination charges", BigDecimal.ONE, request.getDestinationCharges(), true, 3));
        lines.add(line(proformaId, "COST", "HDL", "Handling charges", BigDecimal.ONE, request.getHandlingCharges(), true, 4));
        lines.add(line(proformaId, "COST", "DOC", "Documentation charges", BigDecimal.ONE, request.getDocumentationCharges(), true, 5));
        lines.add(line(proformaId, "COST", "CUS", "Customs charges", BigDecimal.ONE, request.getCustomsCharges(), true, 6));
        lines.add(line(proformaId, "COST", "INS", "Insurance charges", BigDecimal.ONE, request.getInsuranceCharges(), true, 7));
        lines.add(line(proformaId, "COST", "OTH", "Other charges", BigDecimal.ONE, request.getOtherCharges(), true, 8));
        lines.add(line(proformaId, "COMMISSION", "COM", "Commission", BigDecimal.ONE, request.getCommissionAmount(), true, 9));
        lines.add(line(proformaId, "SELL", "MRG", "Margin", BigDecimal.ONE, request.getMarginAmount(), true, 10));

        return lines;
    }

    private TypedProformaChargeLine line(
            UUID proformaId,
            String lineGroup,
            String code,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            boolean editable,
            int sortOrder
    ) {
        BigDecimal safeQuantity = defaultIfNull(quantity);
        BigDecimal safeUnitPrice = defaultIfNull(unitPrice);

        TypedProformaChargeLine line = new TypedProformaChargeLine();
        line.setId(UUID.randomUUID());
        line.setProformaId(proformaId);
        line.setLineGroup(lineGroup);
        line.setCode(code);
        line.setDescription(description);
        line.setQuantity(safeQuantity);
        line.setUnitPrice(safeUnitPrice);
        line.setTotal(safeQuantity.multiply(safeUnitPrice));
        line.setEditable(editable);
        line.setSortOrder(sortOrder);

        return line;
    }
}