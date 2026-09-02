package com.genuino.crm.quoting.common.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class GenuinoProformaPdfTemplate {

    public static final Color PURPLE =
            new Color(62, 48, 104);

    public static final Color ORANGE =
            new Color(241, 140, 38);

    public static final Color YELLOW =
            new Color(255, 196, 55);

    public static final Color LIGHT =
            new Color(243, 246, 249);

    public static final Color DARK =
            new Color(42, 42, 42);

    public static final Color BORDER =
            new Color(185, 190, 198);

    private GenuinoProformaPdfTemplate() {}

    public static Font titleFont() {
        return new Font(
                Font.HELVETICA,
                22,
                Font.BOLD,
                DARK
        );
    }

    public static Font sectionFont() {
        return new Font(
                Font.HELVETICA,
                9,
                Font.BOLD,
                Color.WHITE
        );
    }

    public static Font labelFont() {
        return new Font(
                Font.HELVETICA,
                8,
                Font.BOLD,
                DARK
        );
    }

    public static Font textFont() {
        return new Font(
                Font.HELVETICA,
                8,
                Font.NORMAL,
                DARK
        );
    }

    public static Font smallFont() {
        return new Font(
                Font.HELVETICA,
                7,
                Font.NORMAL,
                Color.DARK_GRAY
        );
    }

    public static void addSectionTitle(
            Document document,
            String title,
            Color color
    ) throws DocumentException {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        title,
                        sectionFont()
                )
        );

        cell.setBackgroundColor(color);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        table.addCell(cell);
        document.add(table);
    }

    public static PdfPCell infoCell(
            String label,
            Object value
    ) {

        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT);

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

    public static PdfPCell headerCell(
            String text
    ) {

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text,
                        sectionFont()
                )
        );

        cell.setBackgroundColor(ORANGE);
        cell.setPadding(6);

        return cell;
    }

    public static PdfPCell valueCell(
            Object value,
            int alignment
    ) {

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        safe(value),
                        textFont()
                )
        );

        cell.setPadding(6);
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(alignment);

        return cell;
    }

    public static PdfPCell totalCell(
            Object value,
            int alignment
    ) {

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        safe(value),
                        labelFont()
                )
        );

        cell.setPadding(7);
        cell.setBackgroundColor(YELLOW);
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(alignment);

        return cell;
    }

    public static Paragraph spacer(int size) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(size);
        return p;
    }

    public static String money(
            BigDecimal value
    ) {

        if (value == null) {
            return "0,00";
        }

        DecimalFormatSymbols symbols =
                new DecimalFormatSymbols();

        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter =
                new DecimalFormat(
                        "#,##0.00",
                        symbols
                );

        return formatter.format(
                value.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );
    }

    public static String safe(Object value) {
        return value == null
                ? "-"
                : value.toString();
    }
}