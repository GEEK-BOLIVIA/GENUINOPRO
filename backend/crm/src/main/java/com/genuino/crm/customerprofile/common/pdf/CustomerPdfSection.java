package com.genuino.crm.quoting.common.pdf;

import com.genuino.crm.customerprofile.domain.CustomerType;
import com.genuino.crm.customerprofile.domain.ProformaCustomerSnapshot;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import java.awt.Color;

public final class CustomerPdfSection {

    private static final Color NAVY =
            new Color(15, 23, 42);

    private static final Color LIGHT =
            new Color(248, 250, 252);

    private static final Color BORDER =
            new Color(226, 232, 240);

    private CustomerPdfSection() {
    }

    public static void add(
            Document document,
            ProformaCustomerSnapshot snapshot,
            Font whiteFont,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        if (snapshot == null) {
            return;
        }

        addSectionTitle(
                document,
                "DATOS DEL CLIENTE",
                whiteFont
        );

        if (CustomerType.NATURAL_PERSON.equals(
                snapshot.getCustomerType()
        )) {
            addNaturalPerson(
                    document,
                    snapshot,
                    labelFont,
                    textFont
            );

            return;
        }

        if (CustomerType.COMPANY.equals(
                snapshot.getCustomerType()
        )) {
            addCompany(
                    document,
                    snapshot,
                    labelFont,
                    textFont
            );
        }
    }

    private static void addNaturalPerson(
            Document document,
            ProformaCustomerSnapshot snapshot,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        table.addCell(infoBox(
                "Tipo de cliente",
                "Persona natural",
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Nombre completo",
                snapshot.getFullName(),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Ciudad",
                buildCity(snapshot),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Número de celular",
                snapshot.getMobilePhone(),
                labelFont,
                textFont
        ));

        document.add(table);
    }

    private static void addCompany(
            Document document,
            ProformaCustomerSnapshot snapshot,
            Font labelFont,
            Font textFont
    ) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        table.addCell(infoBox(
                "Tipo de cliente",
                "Empresa",
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Razón social",
                snapshot.getLegalName(),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "NIT",
                snapshot.getTaxId(),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Teléfono",
                snapshot.getCompanyPhone(),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Ciudad",
                buildCity(snapshot),
                labelFont,
                textFont
        ));

        table.addCell(infoBox(
                "Representante legal",
                snapshot.getLegalRepresentativeName(),
                labelFont,
                textFont
        ));

        PdfPCell addressCell = infoBox(
                "Dirección",
                snapshot.getAddressText(),
                labelFont,
                textFont
        );
        addressCell.setColspan(2);
        table.addCell(addressCell);

        if (
            snapshot.getMapsUrl() != null
            && !snapshot.getMapsUrl().isBlank()
        ) {
            PdfPCell mapsCell = infoBox(
                    "Google Maps",
                    snapshot.getMapsUrl(),
                    labelFont,
                    textFont
            );
            mapsCell.setColspan(2);
            table.addCell(mapsCell);
        }

        document.add(table);
    }

    private static void addSectionTitle(
            Document document,
            String title,
            Font whiteFont
    ) throws DocumentException {

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(
                new Phrase(title, whiteFont)
        );

        cell.setBackgroundColor(NAVY);
        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );
        cell.setPadding(8);

        titleTable.addCell(cell);
        document.add(titleTable);
    }

    private static PdfPCell infoBox(
            String label,
            Object value,
            Font labelFont,
            Font textFont
    ) {
        PdfPCell cell = new PdfPCell();

        cell.setPadding(9);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT);

        cell.addElement(
                new Paragraph(label, labelFont)
        );

        cell.addElement(
                new Paragraph(safe(value), textFont)
        );

        return cell;
    }

    private static String buildCity(
            ProformaCustomerSnapshot snapshot
    ) {
        String city = safe(snapshot.getCityName());
        String department =
                safe(snapshot.getDepartmentName());

        if ("-".equals(city)) {
            return "-";
        }

        if ("-".equals(department)) {
            return city;
        }

        return city + " — " + department;
    }

    private static String safe(Object value) {
        if (value == null) {
            return "-";
        }

        String text = value.toString().trim();

        return text.isEmpty() ? "-" : text;
    }
}