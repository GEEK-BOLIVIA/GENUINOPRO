package com.genuino.crm.quoting.air.pdf;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.air.dto.AirCalculationRequest;
import com.genuino.crm.quoting.air.dto.AirCalculationResponse;
import com.genuino.crm.quoting.air.dto.TypedAirProformaDetailResponse;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfBranding;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfPageEvent;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TypedAirPdfService {

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

    public TypedAirPdfService(
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.customerSnapshotService =
                customerSnapshotService;
    }

    public byte[] generate(
            TypedAirProformaDetailResponse data
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

            AirCalculationRequest input =
                    data.getInput();

            AirCalculationResponse calc =
                    data.getCalculation();

            GenuinoPdfBranding.addHeader(
                    document,
                    "AÉREO",
                    data.getId().toString(),
                    input != null
                            && input.getIssueDate() != null
                            ? input.getIssueDate().toString()
                            : "-",
                    data.getStatus()
            );

            document.add(
                    spacer(2)
            );

            customerSnapshotService
                    .findByProformaId(
                            data.getId()
                    )
                    .ifPresent(snapshot -> {

                        try {

                            CustomerPdfSection.add(
                                    document,
                                    snapshot,
                                    whiteFont,
                                    labelFont,
                                    textFont
                            );

                            document.add(
                                    spacer(2)
                            );

                        } catch (DocumentException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

            if (input != null) {

                addOperationSection(
                        document,
                        input,
                        whiteFont,
                        labelFont,
                        textFont
                );

                document.add(
                        spacer(2)
                );
            }

            if (calc != null) {

                addUsdSection(
                        document,
                        calc,
                        whiteFont,
                        labelFont,
                        textFont
                );

                document.add(
                        spacer(2)
                );

                addCustomsSection(
                        document,
                        calc,
                        whiteFont,
                        labelFont,
                        textFont
                );

                document.add(
                        spacer(2)
                );

                addBoliviaSection(
                        document,
                        calc,
                        whiteFont,
                        labelFont,
                        textFont
                );

              

                addSummary(
                        document,
                        calc,
                        whiteFont,
                        labelFont,
                        textFont
                );

                
            }

            addConditions(
                    document,
                    input,
                    labelFont,
                    textFont
            );

            document.add(
                    spacer(2)
            );

            addFooter(
                    document,
                    smallFont
            );

            document.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "No se pudo generar PDF Aéreo",
                    ex
            );
        }
    }

    private void addOperationSection(
            Document document,
            AirCalculationRequest input,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "DATOS DE OPERACIÓN / PRODUCTO",
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
                        "Peso bruto",
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
                        "Flete aéreo",
                        input.getAirFreightUsd() == null
                                ? "-"
                                : "USD "
                                + money(
                                        input.getAirFreightUsd()
                                ),
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
                        "Teléfono proveedor",
                        input.getSupplierPhone(),
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
                        "T/C comercial",
                        input.getExchangeRate(),
                        labelFont,
                        textFont
                )
        );

        document.add(
                table
        );
    }

    private void addUsdSection(
            Document document,
            AirCalculationResponse calc,
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
                "USD "
                        + money(
                                calc.getFobUsd()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Envío a almacén",
                "USD "
                        + money(
                                calc.getWarehouseShippingUsd()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Comisión bancaria",
                "USD "
                        + money(
                                calc.getBankCommissionUsd()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Flete aéreo",
                "USD "
                        + money(
                                calc.getAirFreightUsd()
                        ),
                textFont,
                labelFont
        );

        addTotalRow(
                table,
                "TOTAL USD",
                "USD "
                        + money(
                                calc.getSubtotalUsd()
                        ),
                labelFont
        );

        document.add(
                table
        );
    }

    private void addCustomsSection(
            Document document,
            AirCalculationResponse calc,
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

        addCompactCustomsRow(
                table,
                "FOB efectos Aduana",
                "USD "
                        + money(
                                calc.getCustomsFobUsd()
                        ),
                "CIF frontera",
                "Bs "
                        + money(
                                calc.getCifBorderBob()
                        ),
                textFont,
                labelFont
        );

        addCompactCustomsRow(
                table,
                "Flete efectos Aduana",
                "USD "
                        + money(
                                calc.getCustomsFreightUsd()
                        ),
                "GA",
                "Bs "
                        + money(
                                calc.getGaBob()
                        ),
                textFont,
                labelFont
        );

        addCompactCustomsRow(
                table,
                "Seguro",
                "USD "
                        + money(
                                calc.getInsuranceUsd()
                        ),
                "IVA",
                "Bs "
                        + money(
                                calc.getIvaBob()
                        ),
                textFont,
                labelFont
        );

        addCompactCustomsRow(
                table,
                "Base imponible",
                "USD "
                        + money(
                                calc.getTaxableBaseUsd()
                        ),
                "ICE",
                "Bs "
                        + money(
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

        totalLabel.setColspan(
                3
        );

        totalLabel.setBackgroundColor(
                YELLOW
        );

        table.addCell(
                totalLabel
        );

        PdfPCell totalValue =
                valueCell(
                        "Bs "
                                + money(
                                        calc.getCustomsTaxesBob()
                                ),
                        labelFont,
                        Element.ALIGN_RIGHT
                );

        totalValue.setBackgroundColor(
                YELLOW
        );

        table.addCell(
                totalValue
        );

        document.add(
                table
        );
    }

    private void addBoliviaSection(
            Document document,
            AirCalculationResponse calc,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "GASTOS AÉREO BOLIVIA",
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
                "Bs "
                        + money(
                                calc.getCustomsTaxesBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Formulario ANB",
                "Bs "
                        + money(
                                calc.getAnbFormBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Almacenaje",
                "Bs "
                        + money(
                                calc.getStorageBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Carpeta",
                "Bs "
                        + money(
                                calc.getFolderBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Operación courier",
                "Bs "
                        + money(
                                calc.getCourierOperationalBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Impuestos Nacionales",
                "Bs "
                        + money(
                                calc.getNationalTaxesBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Agencia despachante",
                "Bs "
                        + money(
                                calc.getDispatchAgencyCommissionBob()
                        ),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Comisión Genuino Importaciones",
                "Bs "
                        + money(
                                calc.getGenuinoCommissionBob()
                        ),
                textFont,
                labelFont
        );

        addTotalRow(
                table,
                "TOTAL COSTOS BOLIVIA",
                "Bs "
                        + money(
                                calc.getTotalBoliviaBob()
                        ),
                labelFont
        );

        document.add(
                table
        );
    }

    private void addSummary(
            Document document,
            AirCalculationResponse calc,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        PdfPTable titleTable =
                new PdfPTable(1);

        titleTable.setWidthPercentage(
                100
        );

        PdfPCell titleCell =
                new PdfPCell(
                        new Phrase(
                                "RESUMEN FINAL",
                                whiteFont
                        )
                );

        titleCell.setBackgroundColor(
                ORANGE
        );

        titleCell.setPadding(
                5
        );

        titleCell.setBorder(
                Rectangle.NO_BORDER
        );

        titleTable.addCell(
                titleCell
        );

        document.add(
                titleTable
        );

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(
                100
        );

        table.setWidths(
                new float[]{
                        1.25f,
                        1f
                }
        );

        PdfPCell left =
                new PdfPCell();

        left.setPadding(
                8
        );

        left.setBackgroundColor(
                LIGHT
        );

        left.setBorderColor(
                BORDER
        );

        left.addElement(
                summaryLine(
                        "Total USD",
                        "USD "
                                + money(
                                        calc.getSubtotalUsd()
                                ),
                        textFont,
                        labelFont
                )
        );

        left.addElement(
                summaryLine(
                        "Costos Bolivia",
                        "Bs "
                                + money(
                                        calc.getTotalBoliviaBob()
                                ),
                        textFont,
                        labelFont
                )
        );

        left.addElement(
                summaryLine(
                        "Pago inicial",
                        "Bs "
                                + money(
                                        calc.getInitialPaymentBob()
                                ),
                        textFont,
                        labelFont
                )
        );

        table.addCell(
                left
        );

        PdfPCell right =
                new PdfPCell();

        right.setPadding(
                9
        );

        right.setBackgroundColor(
                new Color(
                        255,
                        226,
                        196
                )
        );

        right.setBorderColor(
                BORDER
        );

        Paragraph totalLabel =
                new Paragraph(
                        "Inversión total (Bs)",
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

        right.addElement(
                totalLabel
        );

        Paragraph totalValue =
                new Paragraph(
                        "Bs "
                                + money(
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

        right.addElement(
                totalValue
        );

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

        right.addElement(
                unit
        );

        table.addCell(
                right
        );

        document.add(
                table
        );
    }

    private void addConditions(
            Document document,
            AirCalculationRequest input,
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

        table.setWidthPercentage(
                100
        );

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(
                8
        );

        cell.setBackgroundColor(
                LIGHT
        );

        cell.setBorderColor(
                BORDER
        );

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

        table.addCell(
                cell
        );

        document.add(
                table
        );
    }

    private void addFooter(
            Document document,
            Font smallFont
    ) throws DocumentException {

        PdfPTable footer =
                new PdfPTable(1);

        footer.setWidthPercentage(
                100
        );

        PdfPCell cell =
                new PdfPCell();

        cell.setBackgroundColor(
                PURPLE
        );

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        cell.setPaddingTop(
                2
        );

        cell.setPaddingBottom(
                2
        );

        cell.setPaddingLeft(
                6
        );

        cell.setPaddingRight(
                6
        );

        Font whiteSmall =
                new Font(
                        Font.HELVETICA,
                        5.5f,
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

        conditions.setLeading(
                6
        );

        conditions.setSpacingAfter(
                3
        );

        cell.addElement(
                conditions
        );

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

        cell.addElement(
                contact
        );

        footer.addCell(
                cell
        );

        document.add(
                footer
        );
    }

    private void addSectionTitle(
            Document document,
            String title,
            Color color,
            Font whiteFont
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(1);

        table.setWidthPercentage(
                100
        );

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                title,
                                whiteFont
                        )
                );

        cell.setBackgroundColor(
                color
        );

        cell.setPadding(
                6
        );

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        table.addCell(
                cell
        );

        document.add(
                table
        );
    }

    private PdfPTable moneyTable(
            Font whiteFont
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(
                100
        );

        table.setWidths(
                new float[]{
                        4f,
                        1.5f
                }
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

        description.setPadding(
                5
        );

        table.addCell(
                description
        );

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

        total.setPadding(
                5
        );

        total.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        table.addCell(
                total
        );

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

        label.setBackgroundColor(
                YELLOW
        );

        PdfPCell value =
                valueCell(
                        amount,
                        labelFont,
                        Element.ALIGN_RIGHT
                );

        value.setBackgroundColor(
                YELLOW
        );

        table.addCell(
                label
        );

        table.addCell(
                value
        );
    }

    private PdfPCell infoCell(
            String label,
            Object value,
            Font labelFont,
            Font textFont
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(
                4
        );

        cell.setBorderColor(
                BORDER
        );

        cell.setBackgroundColor(
                LIGHT
        );

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

        cell.setPadding(
                4
        );

        cell.setBorderColor(
                BORDER
        );

        cell.setHorizontalAlignment(
                alignment
        );

        return cell;
    }

    private Paragraph spacer(
            int height
    ) {

        Paragraph paragraph =
                new Paragraph(" ");

        paragraph.setSpacingAfter(
                height
        );

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
                .replace(
                        ".",
                        ","
                );
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

        paragraph.setSpacingAfter(
                3
        );

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