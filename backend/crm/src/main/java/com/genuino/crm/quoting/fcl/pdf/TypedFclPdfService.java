package com.genuino.crm.quoting.fcl.pdf;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfBranding;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfPageEvent;
import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

@Service
public class TypedFclPdfService {

    private static final Color PURPLE = new Color(62, 48, 104);
    private static final Color ORANGE = new Color(241, 140, 38);
    private static final Color YELLOW = new Color(255, 196, 55);
    private static final Color LIGHT = new Color(243, 246, 249);
    private static final Color DARK = new Color(42, 42, 42);
    private static final Color BORDER = new Color(190, 195, 202);

    private final ProformaCustomerSnapshotService customerSnapshotService;

    public TypedFclPdfService(
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.customerSnapshotService = customerSnapshotService;
    }

    public byte[] generate(
            TypedFclProforma data,
            List<ProformaAttachment> attachments
    ) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(
                    PageSize.A4,
                    28,
                    28,
                    22,
                    18
            );

            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new GenuinoPdfPageEvent());

            document.open();

            Font labelFont = new Font(
                    Font.HELVETICA,
                    8,
                    Font.BOLD,
                    DARK
            );

            Font textFont = new Font(
                    Font.HELVETICA,
                    8,
                    Font.NORMAL,
                    DARK
            );

            Font whiteFont = new Font(
                    Font.HELVETICA,
                    8,
                    Font.BOLD,
                    Color.WHITE
            );

            Font smallFont = new Font(
                    Font.HELVETICA,
                    7,
                    Font.NORMAL,
                    Color.DARK_GRAY
            );

            GenuinoPdfBranding.addHeader(
                    document,
                    "FCL",
                    data.getId() == null ? "-" : data.getId().toString(),
                    "-",
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
                            throw new RuntimeException(
                                    "No se pudo agregar los datos del cliente al PDF FCL.",
                                    ex
                            );
                        }
                    });

            addOperationSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(2));

            addUsdSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(2));

            addInstallmentsSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(2));

            addCustomsSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(2));

            addBoliviaSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(2));

            addSummary(document, data, whiteFont, labelFont, textFont);

            if (attachments != null && !attachments.isEmpty()) {
                document.add(spacer(2));
                addAttachmentsSection(document, attachments, whiteFont, labelFont, textFont);
            }

            document.add(spacer(2));
            addPaymentConditions(document, data, whiteFont, labelFont, textFont);

            document.add(spacer(2));
            addFooter(document, smallFont);

            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo generar PDF FCL", ex);
        }
    }

    private void addOperationSection(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        addSectionTitle(document, "DATOS DE OPERACIÓN / PRODUCTO", PURPLE, whiteFont);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        table.addCell(infoCell("Producto", data.getProduct(), labelFont, textFont));
        table.addCell(infoCell("Contenedor", data.getContainerType(), labelFont, textFont));
        table.addCell(infoCell("Cantidad", data.getContainerCount(), labelFont, textFont));
        table.addCell(infoCell(
                "Peso total",
                data.getTotalWeightTn() == null ? "-" : format(data.getTotalWeightTn()) + " TN",
                labelFont,
                textFont
        ));

        table.addCell(infoCell("Proveedor", data.getSupplierName(), labelFont, textFont));
        table.addCell(infoCell("Origen", data.getOriginCity(), labelFont, textFont));
        table.addCell(infoCell("Puerto origen", data.getOriginPort(), labelFont, textFont));
        table.addCell(infoCell("Destino", data.getDestinationCity(), labelFont, textFont));

        table.addCell(infoCell(
                "T/C comercial",
                data.getExchangeRateUsed() != null ? data.getExchangeRateUsed() : data.getExchangeRate(),
                labelFont,
                textFont
        ));
        table.addCell(infoCell("T/C impuestos", data.getTaxExchangeRate(), labelFont, textFont));
        table.addCell(infoCell("Pagos FOB", data.getFobPaymentCount(), labelFont, textFont));
        table.addCell(infoCell("Versión reglas", data.getCalculationRuleVersion(), labelFont, textFont));

        document.add(table);
    }

    private void addUsdSection(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        addSectionTitle(document, "EXPRESADO EN DÓLARES AMERICANOS", PURPLE, whiteFont);

        PdfPTable table = moneyTable(whiteFont);

        addMoneyRow(table, "Valor FOB de la mercadería", "USD " + format(data.getFobUsd()), textFont, labelFont);
        addMoneyRow(table, "Comisión giro bancario", "USD " + format(data.getBankTransferCommissionUsd()), textFont, labelFont);
        addMoneyRow(table, "Transporte marítimo", "USD " + format(data.getOriginFreightUsd()), textFont, labelFont);

        if (safeMoney(data.getContainerReleaseUsd()).compareTo(BigDecimal.ZERO) != 0) {
            addMoneyRow(table, "Liberación de contenedor", "USD " + format(data.getContainerReleaseUsd()), textFont, labelFont);
        }

        addTotalRow(table, "TOTAL USD", "USD " + format(data.getSubtotalUsd()), labelFont);
        document.add(table);
    }

    private void addInstallmentsSection(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        int count = data.getFobPaymentCount() == null
                ? 1
                : Math.max(1, Math.min(data.getFobPaymentCount(), 4));

        addSectionTitle(document, "PLAN DE PAGOS FOB", PURPLE, whiteFont);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.5f, 1.5f});

        table.addCell(headerCell("Cuota", whiteFont));
        table.addCell(headerCell("Método", whiteFont));
        table.addCell(headerCell("Monto", whiteFont));

        if (count <= 1) {
            addInstallmentRow(
                    table,
                    "Cuota 1",
                    paymentMethodLabel(data.getPaymentMethod()),
                    data.getFobUsd(),
                    textFont,
                    labelFont
            );
        } else {
            BigDecimal[] amounts = {
                    data.getPayment1AmountUsd(),
                    data.getPayment2AmountUsd(),
                    data.getPayment3AmountUsd(),
                    data.getPayment4AmountUsd()
            };

            String[] methods = {
                    data.getPayment1Method(),
                    data.getPayment2Method(),
                    data.getPayment3Method(),
                    data.getPayment4Method()
            };

            for (int i = 0; i < count; i++) {
                addInstallmentRow(
                        table,
                        "Cuota " + (i + 1),
                        paymentMethodLabel(methods[i]),
                        amounts[i],
                        textFont,
                        labelFont
                );
            }
        }

        PdfPCell totalLabel = valueCell("TOTAL FOB", labelFont, Element.ALIGN_RIGHT);
        totalLabel.setColspan(2);
        totalLabel.setBackgroundColor(YELLOW);

        PdfPCell totalValue = valueCell("USD " + format(data.getFobUsd()), labelFont, Element.ALIGN_RIGHT);
        totalValue.setBackgroundColor(YELLOW);

        table.addCell(totalLabel);
        table.addCell(totalValue);
        document.add(table);
    }

    private void addCustomsSection(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        addSectionTitle(document, "LIQUIDACIÓN ADUANERA", ORANGE, whiteFont);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.2f, 1.2f, 2.2f, 1.2f});

        addCompactCustomsRow(
                table,
                "Seguro",
                "USD " + format(data.getInsuranceUsdCalculated()),
                "CIF frontera",
                "Bs " + format(data.getCifBob()),
                textFont,
                labelFont
        );

        addCompactCustomsRow(table, "GA", percentText(data.getGaPercent()), "GA", "Bs " + format(data.getGaBob()), textFont, labelFont);
        addCompactCustomsRow(table, "IVA", percentText(data.getIvaPercent()), "IVA", "Bs " + format(data.getIvaBob()), textFont, labelFont);
        addCompactCustomsRow(table, "ICE", percentText(data.getIcePercent()), "ICE", "Bs " + format(data.getIceBob()), textFont, labelFont);

        PdfPCell totalLabel = valueCell("TOTAL IMPUESTOS", labelFont, Element.ALIGN_RIGHT);
        totalLabel.setColspan(3);
        totalLabel.setBackgroundColor(YELLOW);
        table.addCell(totalLabel);

        PdfPCell totalValue = valueCell("Bs " + format(data.getCustomsTaxesBob()), labelFont, Element.ALIGN_RIGHT);
        totalValue.setBackgroundColor(YELLOW);
        table.addCell(totalValue);

        document.add(table);
    }

        private void addBoliviaSection(
                Document document,
                TypedFclProforma data,
                Font whiteFont,
                Font labelFont,
                Font textFont
        ) throws DocumentException {

        PdfPTable block = new PdfPTable(1);
        block.setWidthPercentage(100);
        block.setKeepTogether(true);

        PdfPCell titleCell = new PdfPCell(
                new Phrase(
                        "GASTOS FCL BOLIVIA",
                        whiteFont
                )
        );

        titleCell.setBackgroundColor(ORANGE);
        titleCell.setPadding(6);
        titleCell.setBorder(Rectangle.NO_BORDER);

        block.addCell(titleCell);

        PdfPTable table = moneyTable(whiteFont);

        addMoneyRow(
                table,
                "Transporte terrestre",
                "Bs " + format(data.getInlandFreightBob()),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Impuestos a la Aduana Nacional",
                "Bs " + format(data.getCustomsTaxesBob()),
                textFont,
                labelFont
        );

        addMoneyRow(
                table,
                "Gastos despacho / ALBO / DAM",
                "Bs " + format(data.getAlboBob()),
                textFont,
                labelFont
        );

        if (safeMoney(data.getAdaBob())
                .compareTo(BigDecimal.ZERO) != 0) {

                addMoneyRow(
                        table,
                        "ADA",
                        "Bs " + format(data.getAdaBob()),
                        textFont,
                        labelFont
                );
        }

        addMoneyRow(
                table,
                "Agencia despachante",
                "Bs " + format(
                        data.getDispatchAgentCommissionBob()
                ),
                textFont,
                labelFont
        );

        if (safeMoney(data.getExtraNitExpensesBob())
                .compareTo(BigDecimal.ZERO) != 0) {

                addMoneyRow(
                        table,
                        "Gastos extra NIT",
                        "Bs " + format(data.getExtraNitExpensesBob()),
                        textFont,
                        labelFont
                );
        }

        if (safeMoney(data.getMiscellaneousExpensesBob())
                .compareTo(BigDecimal.ZERO) != 0) {

                addMoneyRow(
                        table,
                        "Otros gastos",
                        "Bs " + format(
                                data.getMiscellaneousExpensesBob()
                        ),
                        textFont,
                        labelFont
                );
        }

        addMoneyRow(
                table,
                "Comisión Genuino Importaciones",
                "Bs " + format(data.getGenuinoCommissionBob()),
                textFont,
                labelFont
        );

        BigDecimal totalBolivia = safeMoney(
                data.getTotalOperationBob() != null
                        ? data.getTotalOperationBob()
                        : data.getTotalBob()
        );

        addTotalRow(
                table,
                "TOTAL COSTOS BOLIVIA",
                "Bs " + format(totalBolivia),
                labelFont
        );

        PdfPCell bodyCell = new PdfPCell();
        bodyCell.setBorder(Rectangle.NO_BORDER);
        bodyCell.setPadding(0);
        bodyCell.addElement(table);

        block.addCell(bodyCell);

        document.add(block);
        }

    private void addSummary(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        BigDecimal exchangeRate = safeMoney(
                data.getExchangeRateUsed() != null ? data.getExchangeRateUsed() : data.getExchangeRate()
        );
        BigDecimal usdSubtotal = safeMoney(data.getSubtotalUsd());
        BigDecimal convertedUsd = usdSubtotal.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bolivia = safeMoney(
                data.getTotalOperationBob() != null ? data.getTotalOperationBob() : data.getTotalBob()
        );
        BigDecimal total = convertedUsd.add(bolivia).setScale(2, RoundingMode.HALF_UP);

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        PdfPCell titleCell = new PdfPCell(new Phrase("RESUMEN FINAL", whiteFont));
        titleCell.setBackgroundColor(ORANGE);
        titleCell.setPadding(5);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleTable.addCell(titleCell);
        document.add(titleTable);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.25f, 1f});

        PdfPCell left = new PdfPCell();
        left.setPadding(8);
        left.setBackgroundColor(LIGHT);
        left.setBorderColor(BORDER);
        left.addElement(summaryLine("Total USD", "USD " + format(usdSubtotal), textFont, labelFont));
        left.addElement(summaryLine("Conversión USD a Bs", "Bs " + format(convertedUsd), textFont, labelFont));
        left.addElement(summaryLine("Costos Bolivia", "Bs " + format(bolivia), textFont, labelFont));
        table.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setPadding(9);
        right.setBackgroundColor(new Color(255, 226, 196));
        right.setBorderColor(BORDER);

        Paragraph totalLabel = new Paragraph(
                "Inversión total (Bs)",
                new Font(Font.HELVETICA, 11, Font.BOLD, DARK)
        );
        totalLabel.setAlignment(Element.ALIGN_CENTER);
        right.addElement(totalLabel);

        Paragraph totalValue = new Paragraph(
                "Bs " + format(total),
                new Font(Font.HELVETICA, 15, Font.BOLD, DARK)
        );
        totalValue.setAlignment(Element.ALIGN_CENTER);
        right.addElement(totalValue);

        Paragraph exchange = new Paragraph("T/C comercial: " + format(exchangeRate), labelFont);
        exchange.setAlignment(Element.ALIGN_CENTER);
        right.addElement(exchange);

        table.addCell(right);
        document.add(table);
    }

    private void addPaymentConditions(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        addSectionTitle(document, "CONDICIONES Y FORMAS DE PAGO", PURPLE, whiteFont);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(7);
        cell.setBackgroundColor(LIGHT);
        cell.setBorderColor(BORDER);
        cell.addElement(new Paragraph("IMPORTANTE", labelFont));
        cell.addElement(new Paragraph(
                "Los impuestos a la Aduana Nacional son variables y pueden cambiar durante el proceso. "
                        + "Los pagos de transporte, liberación, despacho y otros conceptos operativos "
                        + "se realizan según la instrucción y el avance de la operación.",
                textFont
        ));
        table.addCell(cell);
        document.add(table);
    }

    private void addFooter(Document document, Font smallFont) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(PURPLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);

        Font whiteSmall = new Font(Font.HELVETICA, 5.5f, Font.NORMAL, Color.WHITE);
        Font whiteBold = new Font(Font.HELVETICA, 7, Font.BOLD, Color.WHITE);

        Paragraph conditions = new Paragraph(
                "El tiempo estimado de entrega está sujeto a la salida de la carga desde origen, "
                        + "producción del proveedor, disponibilidad logística y trámites aduaneros. "
                        + "No contempla retrasos derivados de bloqueos, conflictos, falta de combustible "
                        + "u otros factores externos que afecten el tránsito internacional o nacional.",
                whiteSmall
        );
        conditions.setLeading(6);
        conditions.setSpacingAfter(3);
        cell.addElement(conditions);

        Paragraph contact = new Paragraph(
                "@GenuinoImportaciones     |     76442664     |     "
                        + "Plaza Quintanilla, Torre Attura, Piso 3 Of. 3C - Cochabamba, Bolivia",
                whiteBold
        );
        contact.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(contact);

        footer.addCell(cell);
        document.add(footer);
    }

    private void addAttachmentsSection(
            Document document,
            List<ProformaAttachment> attachments,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        addSectionTitle(document, "PRODUCTO Y PROVEEDOR", PURPLE, whiteFont);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        for (ProformaAttachment attachment : attachments) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(6);
            cell.setBorderColor(BORDER);
            cell.setBackgroundColor(LIGHT);

            String title = attachment.getTitle() != null && !attachment.getTitle().isBlank()
                    ? attachment.getTitle()
                    : "Adjunto de referencia";

            cell.addElement(new Paragraph(title, labelFont));

            boolean isImage =
                    "PRODUCT_IMAGE".equalsIgnoreCase(attachment.getAttachmentType())
                            || "SUPPLIER_IMAGE".equalsIgnoreCase(attachment.getAttachmentType());

            if (isImage && attachment.getAttachmentUrl() != null && !attachment.getAttachmentUrl().isBlank()) {
                try {
                    String relativePath = attachment.getAttachmentUrl().replaceFirst("^/uploads/", "uploads/");
                    Image image = Image.getInstance(relativePath);
                    image.scaleToFit(120, 120);
                    image.setSpacingBefore(4);
                    image.setSpacingAfter(4);
                    cell.addElement(image);
                } catch (Exception ex) {
                    cell.addElement(new Paragraph(
                            "Imagen no disponible: " + attachment.getAttachmentUrl(),
                            textFont
                    ));
                }
            } else if (attachment.getAttachmentUrl() != null && !attachment.getAttachmentUrl().isBlank()) {
                cell.addElement(new Paragraph(attachment.getAttachmentUrl(), textFont));
            }

            if (attachment.getDescription() != null && !attachment.getDescription().isBlank()) {
                Paragraph description = new Paragraph(attachment.getDescription(), textFont);
                description.setSpacingBefore(4);
                cell.addElement(description);
            }

            table.addCell(cell);
        }

        document.add(table);
    }

    private void addSectionTitle(Document document, String title, Color color, Font whiteFont) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, whiteFont));
        cell.setBackgroundColor(color);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        document.add(table);
    }

    private PdfPTable moneyTable(Font whiteFont) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1.5f});

        PdfPCell description = new PdfPCell(new Phrase("DESCRIPCIÓN", whiteFont));
        description.setBackgroundColor(DARK);
        description.setPadding(5);
        table.addCell(description);

        PdfPCell total = new PdfPCell(new Phrase("TOTAL", whiteFont));
        total.setBackgroundColor(DARK);
        total.setPadding(5);
        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
        table.addCell(valueCell(description, textFont, Element.ALIGN_LEFT));
        table.addCell(valueCell(amount, labelFont, Element.ALIGN_RIGHT));
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
        table.addCell(valueCell(label1, textFont, Element.ALIGN_LEFT));
        table.addCell(valueCell(value1, labelFont, Element.ALIGN_RIGHT));
        table.addCell(valueCell(label2, textFont, Element.ALIGN_LEFT));
        table.addCell(valueCell(value2, labelFont, Element.ALIGN_RIGHT));
    }

    private void addTotalRow(
            PdfPTable table,
            String description,
            String amount,
            Font labelFont
    ) {
        PdfPCell label = valueCell(description, labelFont, Element.ALIGN_RIGHT);
        label.setBackgroundColor(YELLOW);
        PdfPCell value = valueCell(amount, labelFont, Element.ALIGN_RIGHT);
        value.setBackgroundColor(YELLOW);
        table.addCell(label);
        table.addCell(value);
    }

    private void addInstallmentRow(
            PdfPTable table,
            String label,
            String method,
            BigDecimal amount,
            Font textFont,
            Font labelFont
    ) {
        table.addCell(valueCell(label, textFont, Element.ALIGN_LEFT));
        table.addCell(valueCell(method, textFont, Element.ALIGN_LEFT));
        table.addCell(valueCell("USD " + format(amount), labelFont, Element.ALIGN_RIGHT));
    }

    private PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(DARK);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell infoCell(String label, Object value, Font labelFont, Font textFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(4);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT);
        cell.addElement(new Paragraph(safe(label), labelFont));
        cell.addElement(new Paragraph(safe(value), textFont));
        return cell;
    }

    private PdfPCell valueCell(Object value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(value), font));
        cell.setPadding(4);
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private Paragraph spacer(int height) {
        Paragraph paragraph = new Paragraph(" ");
        paragraph.setSpacingAfter(height);
        return paragraph;
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String format(BigDecimal value) {
        if (value == null) {
            return "0,00";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private String percentText(BigDecimal value) {
        return value == null ? "0 %" : format(value) + " %";
    }

    private String paymentMethodLabel(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        if ("SWIFT".equalsIgnoreCase(value) || "TRANSFERENCIA".equalsIgnoreCase(value)) {
            return "SWIFT / Transferencia";
        }

        if ("ALIBABA".equalsIgnoreCase(value)) {
            return "Alibaba";
        }

        return value;
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    private Paragraph summaryLine(
            String label,
            String value,
            Font textFont,
            Font labelFont
    ) {
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingAfter(3);
        paragraph.add(new Phrase(label + ": ", textFont));
        paragraph.add(new Phrase(value, labelFont));
        return paragraph;
    }
}