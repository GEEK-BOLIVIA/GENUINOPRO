package com.genuino.crm.quoting.common.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.InputStream;

public final class GenuinoPdfBranding {

    public static final Color PURPLE =
            new Color(48, 37, 104);

    public static final Color PURPLE_DARK =
            new Color(35, 27, 81);

    public static final Color ORANGE =
            new Color(245, 137, 24);

    public static final Color LIGHT_ORANGE =
            new Color(255, 231, 205);

    public static final Color LIGHT =
            new Color(247, 248, 250);

    public static final Color BORDER =
            new Color(205, 209, 216);

    public static final Color DARK =
            new Color(30, 30, 35);

    private GenuinoPdfBranding() {
    }

    public static Font titleFont() {
        return new Font(
                Font.HELVETICA,
                23,
                Font.BOLD,
                PURPLE
        );
    }

    public static Font sectionFont() {
        return new Font(
                Font.HELVETICA,
                8.5f,
                Font.BOLD,
                Color.WHITE
        );
    }

    public static Font labelFont() {
        return new Font(
                Font.HELVETICA,
                7.5f,
                Font.BOLD,
                PURPLE_DARK
        );
    }

    public static Font textFont() {
        return new Font(
                Font.HELVETICA,
                7.5f,
                Font.NORMAL,
                DARK
        );
    }

    public static Font valueFont() {
        return new Font(
                Font.HELVETICA,
                7.5f,
                Font.BOLD,
                DARK
        );
    }

    public static Font footerFont() {
        return new Font(
                Font.HELVETICA,
                6.4f,
                Font.NORMAL,
                Color.WHITE
        );
    }

    public static Image loadLogo() {
        try {
            InputStream input =
                    GenuinoPdfBranding.class
                            .getResourceAsStream(
                                    "/static/pdf/genuino-logo.jpg"
                            );

            if (input == null) {
                return null;
            }

            byte[] bytes =
                    input.readAllBytes();

            Image logo =
                    Image.getInstance(bytes);

            logo.scaleToFit(
                    165,
                    75
            );

            return logo;

        } catch (Exception ex) {
            return null;
        }
    }

    public static void addHeader(
            Document document,
            String proformaType,
            String code,
            String issueDate,
            String status
    ) throws DocumentException {

        PdfPTable header =
                new PdfPTable(2);

        header.setWidthPercentage(100);
        header.setWidths(
                new float[]{1.05f, 1.1f}
        );

        PdfPCell logoCell =
                new PdfPCell();

        logoCell.setBorder(
                Rectangle.NO_BORDER
        );

        logoCell.setPadding(2);

        Image logo = loadLogo();

        if (logo != null) {
            logoCell.addElement(logo);
        } else {
            logoCell.addElement(
                    new Paragraph(
                            "GENUINO IMPORTACIONES",
                            new Font(
                                    Font.HELVETICA,
                                    16,
                                    Font.BOLD,
                                    PURPLE
                            )
                    )
            );
        }

        header.addCell(logoCell);

        PdfPCell right =
                new PdfPCell();

        right.setBorder(
                Rectangle.NO_BORDER
        );

        right.setPaddingLeft(15);

        Paragraph title =
                new Paragraph(
                        "PROFORMA " + proformaType,
                        titleFont()
                );

        title.setAlignment(
                Element.ALIGN_CENTER
        );

        right.addElement(title);

        addMetaLine(
                right,
                "Proforma N°:",
                code
        );

        addMetaLine(
                right,
                "Fecha de emisión:",
                issueDate
        );

        addStatusLine(
                right,
                status
        );

        header.addCell(right);

        document.add(header);
    }

    private static void addMetaLine(
            PdfPCell cell,
            String label,
            String value
    ) {

        Paragraph p =
                new Paragraph();

        p.setAlignment(
                Element.ALIGN_LEFT
        );

        p.setSpacingBefore(3);

        p.add(
                new Phrase(
                        label + "  ",
                        labelFont()
                )
        );

        p.add(
                new Phrase(
                        safe(value),
                        textFont()
                )
        );

        cell.addElement(p);
    }

    private static void addStatusLine(
            PdfPCell cell,
            String status
    ) {

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        PdfPCell label =
                new PdfPCell(
                        new Phrase(
                                "Estado:",
                                labelFont()
                        )
                );

        label.setBorder(
                Rectangle.NO_BORDER
        );

        label.setPadding(4);

        PdfPCell value =
                new PdfPCell(
                        new Phrase(
                                safe(status),
                                new Font(
                                        Font.HELVETICA,
                                        7.5f,
                                        Font.BOLD,
                                        DARK
                                )
                        )
                );

        value.setBackgroundColor(
                new Color(
                        255,
                        199,
                        77
                )
        );

        value.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        value.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        value.setBorder(
                Rectangle.NO_BORDER
        );

        value.setPadding(4);

        table.addCell(label);
        table.addCell(value);

        cell.addElement(table);
    }

    public static void addSectionTitle(
            Document document,
            String title,
            boolean orange
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(1);

        table.setWidthPercentage(100);

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                title,
                                sectionFont()
                        )
                );

        cell.setBackgroundColor(
                orange
                        ? ORANGE
                        : PURPLE
        );

        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(8);

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        table.addCell(cell);

        document.add(table);
    }

    public static PdfPCell infoCell(
            String label,
            Object value
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setBackgroundColor(LIGHT);
        cell.setBorderColor(BORDER);

        cell.setPaddingTop(6);
        cell.setPaddingBottom(6);
        cell.setPaddingLeft(7);
        cell.setPaddingRight(7);

        cell.addElement(
                new Paragraph(
                        safe(label),
                        labelFont()
                )
        );

        cell.addElement(
                new Paragraph(
                        safe(value),
                        textFont()
                )
        );

        return cell;
    }

    public static PdfPCell tableHeader(
            String value,
            int alignment
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                value,
                                sectionFont()
                        )
                );

        cell.setBackgroundColor(
                PURPLE_DARK
        );

        cell.setHorizontalAlignment(
                alignment
        );

        cell.setPadding(5);

        return cell;
    }

    public static PdfPCell valueCell(
            Object value,
            int alignment
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safe(value),
                                textFont()
                        )
                );

        cell.setBorderColor(BORDER);

        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);

        cell.setHorizontalAlignment(
                alignment
        );

        return cell;
    }

    public static PdfPCell boldValueCell(
            Object value,
            int alignment
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safe(value),
                                valueFont()
                        )
                );

        cell.setBorderColor(BORDER);

        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);

        cell.setHorizontalAlignment(
                alignment
        );

        return cell;
    }

    public static PdfPCell totalCell(
            Object value,
            int alignment
    ) {

        PdfPCell cell =
                boldValueCell(
                        value,
                        alignment
                );

        cell.setBackgroundColor(
                new Color(
                        255,
                        223,
                        178
                )
        );

        return cell;
    }

    public static void addCorporateFooter(
            Document document,
            String deliveryText
    ) throws DocumentException {

        PdfPTable footer =
                new PdfPTable(1);

        footer.setWidthPercentage(100);

        PdfPCell cell =
                new PdfPCell();

        cell.setBackgroundColor(
                PURPLE_DARK
        );

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        cell.setPaddingLeft(14);
        cell.setPaddingRight(14);

        Paragraph disclaimer =
                new Paragraph(
                        deliveryText,
                        footerFont()
                );

        disclaimer.setLeading(8);
        disclaimer.setSpacingAfter(9);

        cell.addElement(disclaimer);

        Paragraph contact =
                new Paragraph(
                        "@GenuinoImportaciones     |     "
                                + "76442664     |     "
                                + "Plaza Quintanilla, Torre Attura, "
                                + "Piso 3 Of. 3C - Cochabamba, Bolivia",
                        new Font(
                                Font.HELVETICA,
                                7,
                                Font.BOLD,
                                Color.WHITE
                        )
                );

        contact.setAlignment(
                Element.ALIGN_CENTER
        );

        cell.addElement(contact);

        footer.addCell(cell);

        document.add(footer);
    }

    public static Paragraph spacer(
            float height
    ) {

        Paragraph p =
                new Paragraph(" ");

        p.setSpacingAfter(height);

        return p;
    }

    public static String safe(
            Object value
    ) {
        return value == null
                ? "-"
                : value.toString();
    }
}