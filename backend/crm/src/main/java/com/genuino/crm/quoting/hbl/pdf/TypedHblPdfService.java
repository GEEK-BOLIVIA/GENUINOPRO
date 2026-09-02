package com.genuino.crm.quoting.hbl.pdf;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;
import com.genuino.crm.quoting.hbl.dto.HblCalculationRequest;
import com.genuino.crm.quoting.hbl.dto.HblCalculationResponse;
import com.genuino.crm.quoting.hbl.dto.TypedHblProformaDetailResponse;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.genuino.crm.quoting.common.pdf.GenuinoPdfBranding;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfPageEvent;




@Service
public class TypedHblPdfService {

    private static final Color PURPLE =
            new Color(62, 48, 104);

    private static final Color ORANGE =
            new Color(241, 140, 38);

    private static final Color YELLOW =
            new Color(255, 196, 55);

    private static final Color LIGHT =
            new Color(243, 246, 249);

    private static final Color DARK =
            new Color(42, 42, 42);

    private static final Color BORDER =
            new Color(190, 195, 202);

    private final ProformaCustomerSnapshotService customerSnapshotService;

    public TypedHblPdfService(
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.customerSnapshotService =
                customerSnapshotService;
    }

    public byte[] generate(
            TypedHblProformaDetailResponse data
    ) {

        try {
            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                new Document(
                        PageSize.A4,
                        28,
                        28,
                        22,
                        18
                );

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            out
                    );

            writer.setPageEvent(
                    new GenuinoPdfPageEvent()
            );

            document.open();

            Font titleFont =
                    new Font(
                            Font.HELVETICA,
                            22,
                            Font.BOLD,
                            DARK
                    );

            Font brandFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD,
                            PURPLE
                    );

            Font orangeFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD,
                            ORANGE
                    );

            Font labelFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD,
                            DARK
                    );

            Font textFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.NORMAL,
                            DARK
                    );

            Font whiteFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD,
                            Color.WHITE
                    );

            Font smallFont =
                    new Font(
                            Font.HELVETICA,
                            7,
                            Font.NORMAL,
                            Color.DARK_GRAY
                    );

            HblCalculationRequest input =
                    data.getInput();

            HblCalculationResponse calc =
                    data.getCalculation();

            GenuinoPdfBranding.addHeader(
                    document,
                    "HBL",
                    data.getId().toString(),
                    input.getIssueDate() != null
                            ? input.getIssueDate().toString()
                            : "-",
                    data.getStatus()
            );

            document.add(spacer(2));

            customerSnapshotService
                    .findByProformaId(data.getId())
                    .ifPresent(snapshot -> {
                        try {
                            CustomerPdfSection.add(
                                    document,
                                    snapshot,
                                    whiteFont,
                                    labelFont,
                                    textFont
                            );

                            document.add(spacer(2));

                        } catch (DocumentException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

            addProductSection(
                    document,
                    input,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addUsdSection(
                    document,
                    calc,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addCustomsSection(
                    document,
                    calc,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addBoliviaSection(
                    document,
                    calc,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addSummary(
                    document,
                    calc,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addConditions(
                    document,
                    input,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addFooter(
                    document,
                    smallFont
            );

            document.close();

            return out.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException(
                    "No se pudo generar PDF HBL",
                    ex
            );
        }
    }

    private void addHeader(
            Document document,
            TypedHblProformaDetailResponse data,
            Font brandFont,
            Font orangeFont,
            Font titleFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);
        table.setWidths(
                new float[]{1.1f, 1f}
        );

        PdfPCell brand =
                noBorderCell();

        Paragraph brandText =
                new Paragraph();

        brandText.add(
                new Phrase(
                        "GENUINO ",
                        brandFont
                )
        );

        brandText.add(
                new Phrase(
                        "IMPORTACIONES",
                        orangeFont
                )
        );

        brand.addElement(brandText);

        table.addCell(brand);

        PdfPCell meta =
                noBorderCell();

        Paragraph title =
                new Paragraph(
                        "PROFORMA HBL",
                        titleFont
                );

        title.setAlignment(
                Element.ALIGN_RIGHT
        );

        meta.addElement(title);

        addRightLine(
                meta,
                "Proforma N°:",
                safe(data.getId()),
                labelFont,
                textFont
        );

        addRightLine(
                meta,
                "Fecha de emisión:",
                data.getInput() != null
                        ? safe(
                                data.getInput()
                                        .getIssueDate()
                        )
                        : "-",
                labelFont,
                textFont
        );

        addRightLine(
                meta,
                "Estado:",
                safe(data.getStatus()),
                labelFont,
                textFont
        );

        table.addCell(meta);

        document.add(table);
    }

    private void addProductSection(
            Document document,
            HblCalculationRequest input,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "DATOS DE ENVÍO / PRODUCTO",
                PURPLE,
                whiteFont
        );

        PdfPTable table =
                new PdfPTable(4);

        table.setWidthPercentage(100);

        table.addCell(
                infoCell(
                        "Producto",
                        input.getProductName(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Cantidad",
                        input.getQuantity(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Peso",
                        input.getGrossWeightKg() == null
                                ? "-"
                                : input.getGrossWeightKg()
                                        + " kg",
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Volumen",
                        input.getVolumeCbm() == null
                                ? "-"
                                : input.getVolumeCbm()
                                        + " CBM",
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Proveedor",
                        input.getSupplierName(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Método de pago",
                        input.getPaymentMethod(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "NIT importador",
                        input.getImporterNitType(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "T/C comercial",
                        input.getExchangeRate(),
                        labelFont,
                        textFont
                )
        );

        document.add(table);
    }

    private void addUsdSection(
            Document document,
            HblCalculationResponse calc,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "EXPRESADO EN DÓLARES AMERICANOS",
                PURPLE,
                whiteFont
        );

        PdfPTable table =
                moneyTable(
                        whiteFont
                );

        addMoneyRow(
                table,
                "Valor FOB",
                "USD " + money(calc.getFobUsd()),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Comisión giro bancario",
                "USD " + money(
                        calc.getBankTransferCommissionUsd()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Flete HBL",
                "USD " + money(
                        calc.getMaritimeLandFreightUsd()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Recargo producto sensible",
                "USD " + money(
                        calc.getSensitiveProductSurchargeUsd()
                ),
                textFont,
                labelFont
        );

        addTotalRow(
                table,
                "TOTAL USD",
                "USD " + money(
                        calc.getSubtotalUsd()
                ),
                labelFont
        );

        document.add(table);
    }

private void addCustomsSection(
        Document document,
        HblCalculationResponse calc,
        Font whiteFont,
        Font labelFont,
        Font textFont
) throws DocumentException {

    addSectionTitle(
            document,
            "LIQUIDACIÓN ADUANERA",
            ORANGE,
            whiteFont
    );

    PdfPTable table =
            new PdfPTable(4);

    table.setWidthPercentage(100);

    table.setWidths(
            new float[]{
                    2.2f,
                    1.2f,
                    2.2f,
                    1.2f
            }
    );

    // FILA 1
    addCompactCustomsRow(
            table,
            "FOB efectos Aduana",
            "USD " + money(
                    calc.getCustomsFobUsd()
            ),
            "CIF frontera",
            "Bs " + money(
                    calc.getCifBorderBob()
            ),
            textFont,
            labelFont
    );

    // FILA 2
    addCompactCustomsRow(
            table,
            "Flete efectos Aduana",
            "USD " + money(
                    calc.getCustomsFreightUsd()
            ),
            "GA",
            "Bs " + money(
                    calc.getGaBob()
            ),
            textFont,
            labelFont
    );

    // FILA 3
    addCompactCustomsRow(
            table,
            "Seguro",
            "USD " + money(
                    calc.getInsuranceUsd()
            ),
            "IVA",
            "Bs " + money(
                    calc.getIvaBob()
            ),
            textFont,
            labelFont
    );

    // FILA 4
    addCompactCustomsRow(
            table,
            "Base imponible",
            "USD " + money(
                    calc.getTaxableBaseUsd()
            ),
            "ICE",
            "Bs " + money(
                    calc.getIceBob()
            ),
            textFont,
            labelFont
    );

    PdfPCell totalLabel =
            valueCell(
                    "TOTAL IMPUESTOS",
                    labelFont,
                    Element.ALIGN_RIGHT
            );

    totalLabel.setColspan(3);
    totalLabel.setBackgroundColor(
            YELLOW
    );

    table.addCell(totalLabel);

    PdfPCell totalValue =
            valueCell(
                    "Bs " + money(
                            calc.getCustomsTaxesBob()
                    ),
                    labelFont,
                    Element.ALIGN_RIGHT
            );

    totalValue.setBackgroundColor(
            YELLOW
    );

    table.addCell(totalValue);

    document.add(table);
}

    private void addBoliviaSection(
            Document document,
            HblCalculationResponse calc,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "EXPRESADO EN BOLIVIANOS",
                ORANGE,
                whiteFont
        );

        PdfPTable table =
                moneyTable(
                        whiteFont
                );

        addMoneyRow(
                table,
                "Impuestos Aduana Nacional",
                "Bs " + money(
                        calc.getCustomsTaxesBob()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "ALBO / despacho",
                "Bs " + money(
                        calc.getAlboCustomsClearanceBob()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Comisión despachante",
                "Bs " + money(
                        calc.getDispatchAgentCommissionBob()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Gastos adicionales NIT",
                "Bs " + money(
                        calc.getExtraNitExpensesBob()
                ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Comisión Genuino Importaciones",
                "Bs " + money(
                        calc.getGenuinoCommissionBob()
                ),
                textFont,
                labelFont
        );

        addTotalRow(
                table,
                "TOTAL COSTOS BOLIVIA",
                "Bs " + money(
                        calc.getTotalBoliviaBob()
                ),
                labelFont
        );

        document.add(table);
    }

private void addSummary(
        Document document,
        HblCalculationResponse calc,
        Font whiteFont,
        Font labelFont,
        Font textFont
) throws DocumentException {

    PdfPTable titleTable =
            new PdfPTable(1);

    titleTable.setWidthPercentage(100);

    PdfPCell titleCell =
            new PdfPCell(
                    new Phrase(
                            "RESUMEN FINAL",
                            whiteFont
                    )
            );

    titleCell.setBackgroundColor(ORANGE);
    titleCell.setPadding(5);
    titleCell.setBorder(Rectangle.NO_BORDER);

    titleTable.addCell(titleCell);
    document.add(titleTable);

    PdfPTable table =
            new PdfPTable(2);

    table.setWidthPercentage(100);
    table.setWidths(
            new float[]{1.25f, 1f}
    );

    // IZQUIERDA
    PdfPCell left =
            new PdfPCell();

    left.setPadding(8);
    left.setBackgroundColor(LIGHT);
    left.setBorderColor(BORDER);

    left.addElement(
            summaryLine(
                    "Total USD",
                    "USD " + money(calc.getSubtotalUsd()),
                    textFont,
                    labelFont
            )
    );

    left.addElement(
            summaryLine(
                    "Costos Bolivia",
                    "Bs " + money(calc.getTotalBoliviaBob()),
                    textFont,
                    labelFont
            )
    );

    table.addCell(left);

    // DERECHA
    PdfPCell right =
            new PdfPCell();

    right.setPadding(9);
    right.setBackgroundColor(
            new Color(255, 226, 196)
    );

    right.setBorderColor(BORDER);

    Paragraph totalLabel =
            new Paragraph(
                    "Total general (Bs)",
                    new Font(
                            Font.HELVETICA,
                            11,
                            Font.BOLD,
                            DARK
                    )
            );

    totalLabel.setAlignment(
            Element.ALIGN_CENTER
    );

    right.addElement(totalLabel);

    Paragraph totalValue =
            new Paragraph(
                    "Bs " + money(
                            calc.getTotalBob()
                    ),
                    new Font(
                            Font.HELVETICA,
                            15,
                            Font.BOLD,
                            DARK
                    )
            );

    totalValue.setAlignment(
            Element.ALIGN_CENTER
    );

    right.addElement(totalValue);

    Paragraph unit =
            new Paragraph(
                    "Precio unitario: Bs "
                            + money(
                                    calc.getUnitPriceBob()
                            ),
                    labelFont
            );

    unit.setAlignment(
            Element.ALIGN_CENTER
    );

    right.addElement(unit);

    table.addCell(right);

    document.add(table);
}

    private void addConditions(
            Document document,
            HblCalculationRequest input,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        if (input == null) {
            return;
        }

        if (input.getCommercialTerms() == null
                || input.getCommercialTerms().isBlank()) {
            return;
        }

        PdfPTable table =
                new PdfPTable(1);

        table.setWidthPercentage(100);

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(8);
        cell.setBackgroundColor(LIGHT);
        cell.setBorderColor(BORDER);

        cell.addElement(
                new Paragraph(
                        "CONDICIONES COMERCIALES",
                        labelFont
                )
        );

        cell.addElement(
                new Paragraph(
                        input.getCommercialTerms(),
                        textFont
                )
        );

        table.addCell(cell);

        document.add(table);
    }

 private void addFooter(
        Document document,
        Font smallFont
) throws DocumentException {

    PdfPTable footer =
            new PdfPTable(1);

    footer.setWidthPercentage(100);

    PdfPCell cell =
            new PdfPCell();

    cell.setBackgroundColor(PURPLE);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingTop(3);
    cell.setPaddingBottom(3);
    cell.setPaddingLeft(6);
    cell.setPaddingRight(6);

    Font whiteSmall =
            new Font(
                    Font.HELVETICA,
                    6.5f,
                    Font.NORMAL,
                    Color.WHITE
            );

    Font whiteBold =
            new Font(
                    Font.HELVETICA,
                    7,
                    Font.BOLD,
                    Color.WHITE
            );

    Paragraph conditions =
            new Paragraph(
                    "El tiempo estimado de entrega está sujeto a la salida de la carga desde origen, "
                            + "producción del proveedor, disponibilidad logística y trámites aduaneros. "
                            + "No contempla retrasos derivados de bloqueos, conflictos, falta de combustible "
                            + "u otros factores externos que afecten el tránsito internacional o nacional.",
                    whiteSmall
            );

    conditions.setLeading(8);
    conditions.setSpacingAfter(8);

    cell.addElement(conditions);

    Paragraph contact =
            new Paragraph(
                    "@GenuinoImportaciones     |     "
                            + "76442664     |     "
                            + "Plaza Quintanilla, Torre Attura, "
                            + "Piso 3 Of. 3C - Cochabamba, Bolivia",
                    whiteBold
            );

    contact.setAlignment(
            Element.ALIGN_CENTER
    );

    cell.addElement(contact);

    footer.addCell(cell);

    document.add(footer);
}

    private void addSectionTitle(
            Document document,
            String title,
            Color color,
            Font whiteFont
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(1);

        table.setWidthPercentage(100);

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                title,
                                whiteFont
                        )
                );

        cell.setBackgroundColor(color);
        cell.setPadding(6);
        cell.setBorder(
                Rectangle.NO_BORDER
        );

        table.addCell(cell);

        document.add(table);
    }

    private PdfPTable moneyTable(
            Font whiteFont
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.setWidths(
                new float[]{4f, 1.5f}
        );

        PdfPCell description =
                new PdfPCell(
                        new Phrase(
                                "DESCRIPCIÓN",
                                whiteFont
                        )
                );

        description.setBackgroundColor(
                DARK
        );

        description.setPadding(5);

        table.addCell(description);

        PdfPCell total =
                new PdfPCell(
                        new Phrase(
                                "TOTAL",
                                whiteFont
                        )
                );

        total.setBackgroundColor(
                DARK
        );

        total.setPadding(5);
        total.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        table.addCell(total);

        return table;
    }

    private void addMoneyRow(
            PdfPTable table,
            String description,
            String amount,
            Font textFont,
            Font labelFont
    ) {

        table.addCell(
                valueCell(
                        description,
                        textFont,
                        Element.ALIGN_LEFT
                )
        );

        table.addCell(
                valueCell(
                        amount,
                        labelFont,
                        Element.ALIGN_RIGHT
                )
        );
    }

private void addCompactCustomsRow(
        PdfPTable table,
        String label1,
        String value1,
        String label2,
        String value2,
        Font textFont,
        Font labelFont
) {

    table.addCell(
            valueCell(
                    label1,
                    textFont,
                    Element.ALIGN_LEFT
            )
    );

    table.addCell(
            valueCell(
                    value1,
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    table.addCell(
            valueCell(
                    label2,
                    textFont,
                    Element.ALIGN_LEFT
            )
    );

    table.addCell(
            valueCell(
                    value2,
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );
}

private void addTotalRow(
        PdfPTable table,
        String description,
        String amount,
        Font labelFont
) {

    PdfPCell label =
            valueCell(
                    description,
                    labelFont,
                    Element.ALIGN_RIGHT
            );

    label.setBackgroundColor(YELLOW);

    PdfPCell value =
            valueCell(
                    amount,
                    labelFont,
                    Element.ALIGN_RIGHT
            );

    value.setBackgroundColor(YELLOW);

    table.addCell(label);
    table.addCell(value);
}

private PdfPCell infoCell(
        String label,
        Object value,
        Font labelFont,
        Font textFont
) {

    PdfPCell cell = new PdfPCell();

    cell.setPadding(4);
    cell.setBorderColor(BORDER);
    cell.setBackgroundColor(LIGHT);

    cell.addElement(
            new Paragraph(
                    safe(label),
                    labelFont
            )
    );

    cell.addElement(
            new Paragraph(
                    safe(value),
                    textFont
            )
    );

    return cell;
}

private PdfPCell valueCell(
        Object value,
        Font font,
        int alignment
) {

    PdfPCell cell =
            new PdfPCell(
                    new Phrase(
                            safe(value),
                            font
                    )
            );

    cell.setPadding(4);
    cell.setBorderColor(BORDER);
    cell.setHorizontalAlignment(alignment);

    return cell;
}

private PdfPCell noBorderCell() {

    PdfPCell cell = new PdfPCell();

    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPadding(4);

    return cell;
}

private void addRightLine(
        PdfPCell cell,
        String label,
        String value,
        Font labelFont,
        Font textFont
) {

    Paragraph paragraph = new Paragraph();

    paragraph.setAlignment(
            Element.ALIGN_RIGHT
    );

    paragraph.add(
            new Phrase(
                    label + " ",
                    labelFont
            )
    );

    paragraph.add(
            new Phrase(
                    safe(value),
                    textFont
            )
    );

    cell.addElement(paragraph);
}

private Paragraph spacer(int height) {

    Paragraph paragraph =
            new Paragraph(" ");

    paragraph.setSpacingAfter(height);

    return paragraph;
}

private String money(
        BigDecimal value
) {

    if (value == null) {
        return "0,00";
    }

    return value
            .setScale(
                    2,
                    RoundingMode.HALF_UP
            )
            .toPlainString()
            .replace(".", ",");
}

private String safe(
        Object value
) {

    return value == null
            ? "-"
            : value.toString();
}

private Paragraph summaryLine(
        String label,
        String value,
        Font textFont,
        Font labelFont
) {

    Paragraph paragraph =
            new Paragraph();

    paragraph.setSpacingAfter(3);

    paragraph.add(
            new Phrase(
                    label + ": ",
                    textFont
            )
    );

    paragraph.add(
            new Phrase(
                    value,
                    labelFont
            )
    );

    return paragraph;
}

}