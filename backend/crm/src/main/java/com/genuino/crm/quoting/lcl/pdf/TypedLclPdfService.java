package com.genuino.crm.quoting.lcl.pdf;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.dto.ChargeLineResponse;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfBranding;
import com.genuino.crm.quoting.common.pdf.GenuinoPdfPageEvent;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaDetailResponse;

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
import java.util.List;

@Service
public class TypedLclPdfService {

    private static final Color PURPLE = new Color(62, 48, 104);
    private static final Color ORANGE = new Color(241, 140, 38);
    private static final Color YELLOW = new Color(255, 196, 55);
    private static final Color LIGHT = new Color(243, 246, 249);
    private static final Color DARK = new Color(42, 42, 42);
    private static final Color BORDER = new Color(190, 195, 202);

    private final ProformaCustomerSnapshotService customerSnapshotService;

    public TypedLclPdfService(
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.customerSnapshotService = customerSnapshotService;
    }

    public byte[] generate(
            TypedLclProformaDetailResponse data,
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
                    "LCL",
                    data.getId() == null
                            ? "-"
                            : data.getId().toString(),
                    data.getIssueDate() == null
                            ? "-"
                            : data.getIssueDate().toString(),
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

            addOperationSection(
                    document,
                    data,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            List<ChargeLineResponse> lines =
                    data.getChargeLines() == null
                            ? List.of()
                            : data.getChargeLines();

            List<ChargeLineResponse> usdLines =
                    lines.stream()
                            .filter(line ->
                                    List.of(
                                            "FOB",
                                            "GIRO",
                                            "MAR"
                                    ).contains(line.getCode())
                            )
                            .toList();

            List<ChargeLineResponse> bsLines =
                    lines.stream()
                            .filter(line ->
                                    List.of(
                                            "ADU",
                                            "ALBO",
                                            "VAR",
                                            "COM"
                                    ).contains(line.getCode())
                            )
                            .toList();

            BigDecimal usdSubtotal = sum(usdLines);
            BigDecimal bsSubtotal = sum(bsLines);

            BigDecimal exchangeRate =
                    data.getExchangeRate() != null
                            && data.getExchangeRate()
                                    .compareTo(BigDecimal.ZERO) > 0
                            ? data.getExchangeRate()
                            : BigDecimal.TEN;

            BigDecimal convertedUsdToBs =
                    usdSubtotal
                            .multiply(exchangeRate)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal grandTotal =
                    convertedUsdToBs
                            .add(bsSubtotal)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal quantity =
                    data.getPackageCount() == null
                            || data.getPackageCount() <= 0
                            ? BigDecimal.ONE
                            : BigDecimal.valueOf(
                                    data.getPackageCount()
                            );

            BigDecimal unitPrice =
                    grandTotal.divide(
                            quantity,
                            2,
                            RoundingMode.HALF_UP
                    );

            addUsdSection(
                    document,
                    usdLines,
                    usdSubtotal,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addBoliviaSection(
                    document,
                    bsLines,
                    bsSubtotal,
                    whiteFont,
                    labelFont,
                    textFont
            );

            document.add(spacer(2));

            addSummary(
                    document,
                    usdSubtotal,
                    bsSubtotal,
                    grandTotal,
                    unitPrice,
                    whiteFont,
                    labelFont,
                    textFont
            );

            if (data.getCommercialTerms() != null
                    && !data.getCommercialTerms().isBlank()) {

                document.add(spacer(2));

                addConditions(
                        document,
                        data.getCommercialTerms(),
                        labelFont,
                        textFont
                );
            }

            if (attachments != null
                    && !attachments.isEmpty()) {

                document.add(spacer(2));

                addAttachmentsSection(
                        document,
                        attachments,
                        whiteFont,
                        labelFont,
                        textFont
                );
            }

            document.add(spacer(2));

            addFooter(
                    document,
                    smallFont
            );

            document.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "No se pudo generar PDF LCL",
                    ex
            );
        }
    }

    private void addOperationSection(
            Document document,
            TypedLclProformaDetailResponse data,
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

        PdfPTable table = new PdfPTable(4);

        table.setWidthPercentage(100);

        table.addCell(
                infoCell(
                        "Producto",
                        data.getCargoDescription(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Cantidad",
                        data.getPackageCount(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Peso bruto",
                        data.getGrossWeightKg() == null
                                ? "-"
                                : money(data.getGrossWeightKg())
                                + " kg",
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "CBM",
                        data.getVolumeCbm(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Origen",
                        buildLocation(
                                data.getOriginCity(),
                                data.getOriginCountry()
                        ),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "Destino",
                        buildLocation(
                                data.getDestinationCity(),
                                data.getDestinationCountry()
                        ),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "T/C comercial",
                        data.getExchangeRate(),
                        labelFont,
                        textFont
                )
        );

        table.addCell(
                infoCell(
                        "T/C impuestos",
                        data.getTaxExchangeRate(),
                        labelFont,
                        textFont
                )
        );

        document.add(table);
    }

    private void addUsdSection(
            Document document,
            List<ChargeLineResponse> lines,
            BigDecimal subtotal,
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

        PdfPTable table = moneyTable(
                whiteFont
        );

        for (ChargeLineResponse line : lines) {

            addMoneyRow(
                    table,
                    line.getDescription(),
                    "USD "
                            + money(
                                    line.getTotal()
                            ),
                    textFont,
                    labelFont
            );
        }

        addTotalRow(
                table,
                "TOTAL USD",
                "USD "
                        + money(
                                subtotal
                        ),
                labelFont
        );

        document.add(table);
    }

    private void addBoliviaSection(
            Document document,
            List<ChargeLineResponse> lines,
            BigDecimal subtotal,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "GASTOS LCL BOLIVIA",
                ORANGE,
                whiteFont
        );

        PdfPTable table = moneyTable(
                whiteFont
        );

        for (ChargeLineResponse line : lines) {

            addMoneyRow(
                    table,
                    line.getDescription(),
                    "Bs "
                            + money(
                                    line.getTotal()
                            ),
                    textFont,
                    labelFont
            );
        }

        addTotalRow(
                table,
                "TOTAL COSTOS BOLIVIA",
                "Bs "
                        + money(
                                subtotal
                        ),
                labelFont
        );

        document.add(table);
    }

    private void addSummary(
            Document document,
            BigDecimal usdSubtotal,
            BigDecimal bsSubtotal,
            BigDecimal grandTotal,
            BigDecimal unitPrice,
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
                                        usdSubtotal
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
                                        bsSubtotal
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
                                        grandTotal
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
                                        unitPrice
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
            String commercialTerms,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

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
                        commercialTerms,
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

    private void addAttachmentsSection(
            Document document,
            List<ProformaAttachment> attachments,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        addSectionTitle(
                document,
                "PRODUCTO Y PROVEEDOR",
                PURPLE,
                whiteFont
        );

        PdfPTable table =
                new PdfPTable(1);

        table.setWidthPercentage(
                100
        );

        for (ProformaAttachment attachment : attachments) {

            PdfPCell cell =
                    new PdfPCell();

            cell.setPadding(
                    6
            );

            cell.setBorderColor(
                    BORDER
            );

            cell.setBackgroundColor(
                    LIGHT
            );

            String title =
                    attachment.getTitle() != null
                            && !attachment.getTitle().isBlank()
                            ? attachment.getTitle()
                            : "Adjunto de referencia";

            cell.addElement(
                    new Paragraph(
                            title,
                            labelFont
                    )
            );

            boolean isImage =
                    "PRODUCT_IMAGE"
                            .equalsIgnoreCase(
                                    attachment.getAttachmentType()
                            )
                            ||
                            "SUPPLIER_IMAGE"
                                    .equalsIgnoreCase(
                                            attachment.getAttachmentType()
                                    );

            if (isImage
                    && attachment.getAttachmentUrl() != null
                    && !attachment.getAttachmentUrl().isBlank()) {

                try {

                    String relativePath =
                            attachment
                                    .getAttachmentUrl()
                                    .replaceFirst(
                                            "^/uploads/",
                                            "uploads/"
                                    );

                    Image image =
                            Image.getInstance(
                                    relativePath
                            );

                    image.scaleToFit(
                            120,
                            120
                    );

                    image.setSpacingBefore(
                            4
                    );

                    image.setSpacingAfter(
                            4
                    );

                    cell.addElement(
                            image
                    );

                } catch (Exception ex) {

                    cell.addElement(
                            new Paragraph(
                                    "Imagen no disponible: "
                                            + attachment.getAttachmentUrl(),
                                    textFont
                            )
                    );
                }

            } else if (attachment.getAttachmentUrl() != null
                    && !attachment.getAttachmentUrl().isBlank()) {

                cell.addElement(
                        new Paragraph(
                                attachment.getAttachmentUrl(),
                                textFont
                        )
                );
            }

            if (attachment.getDescription() != null
                    && !attachment.getDescription().isBlank()) {

                Paragraph description =
                        new Paragraph(
                                attachment.getDescription(),
                                textFont
                        );

                description.setSpacingBefore(
                        4
                );

                cell.addElement(
                        description
                );
            }

            table.addCell(
                    cell
            );
        }

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

    private BigDecimal sum(
            List<ChargeLineResponse> lines
    ) {

        return lines.stream()
                .map(
                        ChargeLineResponse::getTotal
                )
                .filter(
                        value -> value != null
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
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

    private String buildLocation(
            String city,
            String country
    ) {

        boolean hasCity =
                city != null
                        && !city.isBlank();

        boolean hasCountry =
                country != null
                        && !country.isBlank();

        if (hasCity && hasCountry) {
            return city + " - " + country;
        }

        if (hasCity) {
            return city;
        }

        if (hasCountry) {
            return country;
        }

        return "Sin información";
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