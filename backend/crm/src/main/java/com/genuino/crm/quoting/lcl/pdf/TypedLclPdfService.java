package com.genuino.crm.quoting.lcl.pdf;

import com.genuino.crm.quoting.common.dto.ChargeLineResponse;
import com.genuino.crm.quoting.lcl.dto.TypedLclProformaDetailResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;

@Service
public class TypedLclPdfService {

    private static final Color NAVY = new Color(15, 23, 42);
    private static final Color ORANGE = new Color(249, 115, 22);
    private static final Color LIGHT = new Color(248, 250, 252);
    private static final Color YELLOW = new Color(253, 224, 71);
    private static final Color BORDER = new Color(226, 232, 240);

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
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font brandFont = new Font(Font.HELVETICA, 18, Font.BOLD, NAVY);
            Font orangeFont = new Font(Font.HELVETICA, 18, Font.BOLD, ORANGE);
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, NAVY);
            Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD, NAVY);
            Font textFont = new Font(Font.HELVETICA, 9, Font.NORMAL, NAVY);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);
            Font whiteFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);

            addHeader(document, data, brandFont, orangeFont, titleFont, smallFont);
            document.add(spacer(10));

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

                            document.add(spacer(10));

                        } catch (DocumentException exception) {
                            throw new RuntimeException(
                                    "No se pudo agregar los datos del cliente al PDF LCL.",
                                    exception
                            );
                        }
                    });

                    PdfPTable info = new PdfPTable(2);
                    info.setWidthPercentage(100);
                    info.setWidths(new float[]{1f, 1f});

                    info.addCell(
                            infoBox(
                                    "Origen",
                                    data.getOriginCity(),
                                    labelFont,
                                    textFont
                            )
                    );

                    info.addCell(
                            infoBox(
                                    "Destino",
                                    data.getDestinationCity(),
                                    labelFont,
                                    textFont
                            )
                    );
            document.add(info);
            document.add(spacer(14));

            addAttachmentsSection(document, attachments, whiteFont, labelFont, textFont);
            document.add(spacer(14));

            List<ChargeLineResponse> lines = data.getChargeLines() == null ? List.of() : data.getChargeLines();

            List<ChargeLineResponse> usdLines = lines.stream()
                    .filter(line -> List.of("FOB", "GIRO", "MAR").contains(line.getCode()))
                    .toList();

            List<ChargeLineResponse> bsLines = lines.stream()
                    .filter(line -> List.of("ADU", "ALBO", "VAR", "COM").contains(line.getCode()))
                    .toList();

            BigDecimal usdSubtotal = sum(usdLines);
            BigDecimal bsSubtotal = sum(bsLines);

            BigDecimal exchangeRate = BigDecimal.TEN;
            BigDecimal convertedUsdToBs = usdSubtotal.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal grandTotal = convertedUsdToBs.add(bsSubtotal).setScale(2, RoundingMode.HALF_UP);

            BigDecimal quantity =
                    data.getPackageCount() == null || data.getPackageCount() == 0
                            ? BigDecimal.ONE
                            : BigDecimal.valueOf(data.getPackageCount());

            BigDecimal unitPrice = grandTotal.divide(quantity, 2, RoundingMode.HALF_UP);

            addSection(document, "EXPRESADO EN DÓLARES AMERICANOS", "USD", usdLines, usdSubtotal, whiteFont, labelFont, textFont);
            document.add(spacer(12));

            addSection(document, "EXPRESADO EN BOLIVIANOS", "Bs", bsLines, bsSubtotal, whiteFont, labelFont, textFont);
            document.add(spacer(12));

            addSummary(document, usdSubtotal, convertedUsdToBs, bsSubtotal, grandTotal, unitPrice, whiteFont, labelFont, textFont);

            document.add(spacer(12));
            Paragraph footer = new Paragraph(
                    "Documento generado por Genuino CRM. Proforma sujeta a variación de tipo de cambio, aduana y disponibilidad operativa.",
                    smallFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo generar PDF LCL", ex);
        }
    }

    private void addHeader(Document document, TypedLclProformaDetailResponse data, Font brandFont, Font orangeFont, Font titleFont, Font smallFont) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1.2f, 1f});

        PdfPCell brandCell = noBorderCell();
        Paragraph brand = new Paragraph();
        brand.add(new Phrase("Genuino ", brandFont));
        brand.add(new Phrase("Importaciones", orangeFont));
        brandCell.addElement(brand);
        brandCell.addElement(new Paragraph("Proforma comercial LCL", smallFont));
        header.addCell(brandCell);

        PdfPCell metaCell = noBorderCell();
        Paragraph title = new Paragraph("PROFORMA LCL", titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(title);

        Paragraph id = new Paragraph("ID: " + safe(data.getId()), smallFont);
        id.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(id);

        Paragraph status = new Paragraph("Estado: " + safe(data.getStatus()), smallFont);
        status.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(status);

        header.addCell(metaCell);
        document.add(header);
    }

    private void addSection(
            Document document,
            String title,
            String currency,
            List<ChargeLineResponse> lines,
            BigDecimal subtotal,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        PdfPCell titleCell = new PdfPCell(new Phrase(title, whiteFont));
        titleCell.setBackgroundColor(ORANGE);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(8);
        titleTable.addCell(titleCell);
        document.add(titleTable);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1.5f});

        table.addCell(headerCell("Descripción", whiteFont));
        table.addCell(headerCell("Total", whiteFont));

        for (ChargeLineResponse line : lines) {
            table.addCell(valueCell(line.getDescription(), textFont));
            table.addCell(valueCell(currency + " " + format(line.getTotal()), labelFont, Element.ALIGN_RIGHT));
        }

        PdfPCell totalLabel = valueCell("TOTAL", labelFont, Element.ALIGN_RIGHT);
        totalLabel.setBackgroundColor(YELLOW);
        table.addCell(totalLabel);

        PdfPCell totalValue = valueCell(currency + " " + format(subtotal), labelFont, Element.ALIGN_RIGHT);
        totalValue.setBackgroundColor(YELLOW);
        table.addCell(totalValue);

        document.add(table);
    }

    private void addSummary(
            Document document,
            BigDecimal usdSubtotal,
            BigDecimal convertedUsdToBs,
            BigDecimal bsSubtotal,
            BigDecimal grandTotal,
            BigDecimal unitPrice,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        PdfPCell titleCell = new PdfPCell(new Phrase("RESUMEN FINAL EN BOLIVIANOS", whiteFont));
        titleCell.setBackgroundColor(NAVY);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(8);
        titleTable.addCell(titleCell);
        document.add(titleTable);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1.5f});

        table.addCell(valueCell("Valor FOB + comisión + transporte", textFont));
        table.addCell(valueCell("USD " + format(usdSubtotal), labelFont, Element.ALIGN_RIGHT));

        table.addCell(valueCell("Conversión en bolivianos", textFont));
        table.addCell(valueCell("Bs " + format(convertedUsdToBs), labelFont, Element.ALIGN_RIGHT));

        table.addCell(valueCell("Saldo contraentrega", textFont));
        table.addCell(valueCell("Bs " + format(bsSubtotal), labelFont, Element.ALIGN_RIGHT));

        PdfPCell totalLabel = valueCell("TOTAL GENERAL", labelFont, Element.ALIGN_RIGHT);
        totalLabel.setBackgroundColor(YELLOW);
        table.addCell(totalLabel);

        PdfPCell totalValue = valueCell("Bs " + format(grandTotal), labelFont, Element.ALIGN_RIGHT);
        totalValue.setBackgroundColor(YELLOW);
        table.addCell(totalValue);

        table.addCell(valueCell("Precio unitario", labelFont, Element.ALIGN_RIGHT));
        table.addCell(valueCell("Bs " + format(unitPrice), labelFont, Element.ALIGN_RIGHT));

        document.add(table);
    }

    private BigDecimal sum(List<ChargeLineResponse> lines) {
        return lines.stream()
                .map(ChargeLineResponse::getTotal)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private PdfPCell noBorderCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell infoBox(String label, Object value, Font labelFont, Font textFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(safe(value), textFont));
        return cell;
    }

    private PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(NAVY);
        cell.setPadding(7);
        return cell;
    }

    private PdfPCell valueCell(Object value, Font font) {
        return valueCell(value, font, Element.ALIGN_LEFT);
    }

    private PdfPCell valueCell(Object value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(value), font));
        cell.setPadding(7);
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private Paragraph spacer(int height) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(height);
        return p;
    }

    private String format(Object value) {
        if (value == null) return "0.00";
        if (value instanceof BigDecimal bd) {
            return bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return value.toString();
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    private void addAttachmentsSection(
            Document document,
            List<ProformaAttachment> attachments,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase("PRODUCTO Y PROVEEDOR", whiteFont));
        titleCell.setBackgroundColor(NAVY);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(8);
        titleTable.addCell(titleCell);

        document.add(titleTable);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        for (ProformaAttachment attachment : attachments) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(8);
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
                    String relativePath = attachment
                            .getAttachmentUrl()
                            .replaceFirst("^/uploads/", "uploads/");

                    Image image = Image.getInstance(relativePath);
                    image.scaleToFit(140, 140);
                    image.setSpacingBefore(6);
                    image.setSpacingAfter(6);
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
}