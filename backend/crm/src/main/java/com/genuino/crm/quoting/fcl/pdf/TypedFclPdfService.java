package com.genuino.crm.quoting.fcl.pdf;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.fcl.domain.TypedFclProforma;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.genuino.crm.customerprofile.ProformaCustomerSnapshotService;
import com.genuino.crm.quoting.common.pdf.CustomerPdfSection;

@Service
public class TypedFclPdfService {

    private static final Color NAVY = new Color(15, 23, 42);
    private static final Color ORANGE = new Color(249, 115, 22);
    private static final Color LIGHT = new Color(248, 250, 252);
    private static final Color YELLOW = new Color(253, 224, 71);
    private static final Color BORDER = new Color(226, 232, 240);
    private final ProformaCustomerSnapshotService customerSnapshotService;

    public TypedFclPdfService(
            ProformaCustomerSnapshotService customerSnapshotService
    ) {
        this.customerSnapshotService = customerSnapshotService;
    }

    public byte[] generate(
            TypedFclProforma data,
            java.util.List<ProformaAttachment> attachments
    ) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font brandFont = new Font(Font.HELVETICA, 18, Font.BOLD, NAVY);
            Font orangeFont = new Font(Font.HELVETICA, 18, Font.BOLD, ORANGE);
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, ORANGE);
            Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD, NAVY);
            Font textFont = new Font(Font.HELVETICA, 9, Font.NORMAL, NAVY);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);
            Font whiteFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);

            addHeader(document, data, brandFont, orangeFont, titleFont, smallFont);
            document.add(spacer(10));

            PdfPTable client = new PdfPTable(2);
            client.setWidthPercentage(100);
            client.setWidths(new float[]{1f, 1f});

            client.addCell(infoBox("Asesor", data.getSellerName(), labelFont, textFont));
            client.addCell(infoBox("Destino", data.getDestinationCity(), labelFont, textFont));
            document.add(client);

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
                                    "No se pudo agregar los datos del cliente al PDF FCL.",
                                    exception
                            );
                        }
                    });

            PdfPTable operation = new PdfPTable(4);
            operation.setWidthPercentage(100);
            operation.setWidths(new float[]{1f, 1f, 1f, 1f});
            operation.addCell(infoBox("Proveedor", data.getSupplierName(), labelFont, textFont));
            operation.addCell(infoBox("Origen", data.getOriginCity(), labelFont, textFont));
            operation.addCell(infoBox("Puerto", data.getOriginPort(), labelFont, textFont));
            operation.addCell(infoBox("Producto", data.getProduct(), labelFont, textFont));
            operation.addCell(infoBox("Contenedor", data.getContainerType(), labelFont, textFont));
            operation.addCell(infoBox("Cantidad", data.getContainerCount(), labelFont, textFont));
            operation.addCell(infoBox("Tipo cambio", data.getExchangeRateUsed(), labelFont, textFont));
            
            operation.addCell(infoBox(
                    "T/C impuestos",
                    data.getTaxExchangeRate(),
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Versión reglas",
                    data.getCalculationRuleVersion(),
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Peso total TN",
                    data.getTotalWeightTn(),
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Pagos FOB",
                    data.getFobPaymentCount(),
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Cliente paga USD",
                    Boolean.TRUE.equals(data.getCustomerPaysInUsd()) ? "Sí" : "No",
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Cliente paga proveedor",
                    Boolean.TRUE.equals(data.getCustomerPaysSupplier()) ? "Sí" : "No",
                    labelFont,
                    textFont
            ));

            operation.addCell(infoBox(
                    "Método pago",
                    data.getPaymentMethod(),
                    labelFont,
                    textFont
            ));

           

            document.add(operation);

            document.add(spacer(12));

            addAttachmentsSection(document, attachments, whiteFont, labelFont, textFont);

            document.add(spacer(14));

            addUsdSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(12));

            addFobInstallments(document, data, labelFont, textFont);

            addBobSection(document, data, whiteFont, labelFont, textFont);
            document.add(spacer(4));

            addTotal(document, data, whiteFont, labelFont);

            document.add(spacer(16));

            addPaymentConditionsPage(document, data, whiteFont, labelFont, textFont, smallFont);

            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo generar PDF FCL", ex);
        }
    }

    private void addHeader(Document document, TypedFclProforma data, Font brandFont, Font orangeFont, Font titleFont, Font smallFont) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1.2f, 1f});

        PdfPCell brandCell = noBorderCell();
        Paragraph brand = new Paragraph();
        brand.add(new Phrase("Genuino ", brandFont));
        brand.add(new Phrase("Importaciones", orangeFont));
        brandCell.addElement(brand);
        brandCell.addElement(new Paragraph("Proforma comercial FCL", smallFont));
        header.addCell(brandCell);

        PdfPCell metaCell = noBorderCell();
        Paragraph title = new Paragraph("PROFORMA FCL", titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(title);

        Paragraph code = new Paragraph("Código: " + safe(data.getCode()), smallFont);
        code.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(code);

        Paragraph status = new Paragraph("Estado: " + safe(data.getStatus()), smallFont);
        status.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(status);

        header.addCell(metaCell);
        document.add(header);
    }

    private void addUsdSection(Document document, TypedFclProforma data, Font whiteFont, Font labelFont, Font textFont) throws DocumentException {
        addSectionTitle(document, "EXPRESADO EN DÓLARES AMERICANOS", whiteFont, ORANGE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1.5f});

        table.addCell(headerCell("Descripción", whiteFont));
        table.addCell(headerCell("Total", whiteFont));

        table.addCell(valueCell("Valor FOB de la mercadería", textFont));
        table.addCell(valueCell("USD " + format(data.getFobUsd()), labelFont, Element.ALIGN_RIGHT));

        table.addCell(valueCell("Comisión giro bancario", textFont));
        table.addCell(valueCell("USD " + format(data.getBankTransferCommissionUsd()), labelFont, Element.ALIGN_RIGHT));

        table.addCell(valueCell("Transporte marítimo", textFont));
        table.addCell(valueCell("USD " + format(data.getMaritimeFreightUsd()), labelFont, Element.ALIGN_RIGHT));

        table.addCell(valueCell(
                "Liberación de contenedor",
                textFont
        ));

        table.addCell(valueCell(
                "USD " + format(
                        data.getContainerReleaseUsd()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        ));


        PdfPCell totalLabel = valueCell("TOTAL DE COMPONENTES EN DÓLARES", labelFont, Element.ALIGN_RIGHT);
        totalLabel.setBackgroundColor(YELLOW);
        table.addCell(totalLabel);

        PdfPCell totalValue = valueCell(
                "USD " + format(data.getSubtotalUsd()),
                labelFont,
                Element.ALIGN_RIGHT
        );
        totalValue.setBackgroundColor(YELLOW);
        table.addCell(totalValue);

        document.add(table);
    }

    private void addBobSection(Document document, TypedFclProforma data, Font whiteFont, Font labelFont, Font textFont) throws DocumentException {
        addSectionTitle(document, "EXPRESADO EN BOLIVIANOS", whiteFont, NAVY);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1.5f});

        table.addCell(headerCell("Descripción", whiteFont));
        table.addCell(headerCell("Total", whiteFont));

        addRow(table, "Transporte terrestre", data.getInlandFreightBob(), textFont, labelFont);
        addRow(
                table,
                "Otros gastos",
                data.getMiscellaneousExpensesBob(),
                textFont,
                labelFont
        );
        addRow(table, "Impuestos a la Aduana Nacional", data.getCustomsTaxesBob(), textFont, labelFont);
        addRow(table, "Gastos despacho, ALBO, DAM, etc.", data.getAlboBob(), textFont, labelFont);
        addRow(table, "Comisión agente despachante", data.getDispatchAgentCommissionBob(), textFont, labelFont);
        addRow(table, "Comisión Genuino Importaciones", data.getGenuinoCommissionBob(), textFont, labelFont);
        addRow(table, "Gastos extra NIT", data.getExtraNitExpensesBob(), textFont, labelFont);

        document.add(table);
    }

   private void addTotal(
            Document document,
            TypedFclProforma data,
            Font whiteFont,
            Font labelFont
    ) throws DocumentException {

        BigDecimal boliviaOperationCost =
                safeMoney(
                        data.getTotalOperationBob() != null
                                ? data.getTotalOperationBob()
                                : data.getTotalBob()
                );

        BigDecimal commercialExchangeRate =
                safeMoney(
                        data.getExchangeRateUsed() != null
                                ? data.getExchangeRateUsed()
                                : data.getExchangeRate()
                );

        BigDecimal usdComponents =
                safeMoney(data.getSubtotalUsd());

        BigDecimal totalInvestmentBob =
                usdComponents
                        .multiply(commercialExchangeRate)
                        .add(boliviaOperationCost)
                        .setScale(2, RoundingMode.HALF_UP);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f});

        PdfPCell boliviaLabel = new PdfPCell(
                new Phrase(
                        "COSTOS DE OPERACIÓN EN BOLIVIA",
                        whiteFont
                )
        );
        boliviaLabel.setBackgroundColor(ORANGE);
        boliviaLabel.setPadding(10);
        table.addCell(boliviaLabel);

        PdfPCell boliviaValue = new PdfPCell(
                new Phrase(
                        "Bs " + format(boliviaOperationCost),
                        labelFont
                )
        );
        boliviaValue.setBackgroundColor(YELLOW);
        boliviaValue.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );
        boliviaValue.setPadding(10);
        table.addCell(boliviaValue);

        Font totalFont = new Font(
                Font.HELVETICA,
                12,
                Font.BOLD,
                NAVY
        );

        PdfPCell totalLabel = new PdfPCell(
                new Phrase(
                        "INVERSIÓN REFERENCIAL TOTAL",
                        whiteFont
                )
        );
        totalLabel.setBackgroundColor(NAVY);
        totalLabel.setPadding(12);
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(
                new Phrase(
                        "Bs " + format(totalInvestmentBob),
                        totalFont
                )
        );
        totalValue.setBackgroundColor(YELLOW);
        totalValue.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );
        totalValue.setPadding(12);
        table.addCell(totalValue);

        document.add(table);
    }


    private void addRow(PdfPTable table, String label, BigDecimal value, Font textFont, Font labelFont) {
        table.addCell(valueCell(label, textFont));
        table.addCell(valueCell("Bs " + format(value), labelFont, Element.ALIGN_RIGHT));
    }

    private void addSectionTitle(Document document, String title, Font font, Color color) throws DocumentException {
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        titleTable.addCell(cell);
        document.add(titleTable);
    }

    private PdfPCell noBorderCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell infoBox(String label, Object value, Font labelFont, Font textFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(9);
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

    private String format(BigDecimal value) {

        if (value == null) {
            return "0,00";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter =
                new DecimalFormat("#,##0.00", symbols);

        return formatter.format(
                value.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

private void addFobInstallments(
        Document document,
        TypedFclProforma data,
        Font labelFont,
        Font textFont
) throws DocumentException {

    Integer payments = data.getFobPaymentCount();

    if (payments == null || payments <= 1) {
        return;
    }

    BigDecimal fob = data.getFobUsd() != null
            ? data.getFobUsd()
            : BigDecimal.ZERO;

    BigDecimal installment = fob.divide(
            BigDecimal.valueOf(payments),
            2,
            RoundingMode.HALF_UP
    );

    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setSpacingBefore(8);

    table.addCell(valueCell("CUOTAS FOB", labelFont));
    table.addCell(valueCell("", labelFont));

    String[] labels = {
            "Primera cuota",
            "Segunda cuota",
            "Tercera cuota",
            "Cuarta cuota"
    };

    for (int i = 0; i < payments && i < labels.length; i++) {
        table.addCell(valueCell(labels[i], textFont));
        table.addCell(
                valueCell(
                        "USD " + format(installment),
                        labelFont,
                        Element.ALIGN_RIGHT
                )
        );
    }

    table.addCell(valueCell("TOTAL FOB", labelFont));

    table.addCell(
            valueCell(
                    "USD " + format(fob),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    document.add(table);
}

private void addPaymentConditionsPage(
        Document document,
        TypedFclProforma data,
        Font whiteFont,
        Font labelFont,
        Font textFont,
        Font smallFont
) throws DocumentException {

    addSectionTitle(document, "CONDICIONES Y FORMAS DE PAGO", whiteFont, NAVY);
    document.add(spacer(8));

    Paragraph important = new Paragraph(
            "IMPORTANTE: Estimado cliente, si confirma nuestra proforma para gestionar el proceso de importación, "
                    + "debe considerar que el costo de impuestos a la Aduana es variable. Si existe un reintegro "
                    + "por parte de la Aduana Nacional, el cliente asume la diferencia.",
            textFont
    );
    important.setSpacingAfter(12);
    document.add(important);

    BigDecimal commercialExchangeRate =
            safeMoney(
                    data.getExchangeRateUsed() != null
                            ? data.getExchangeRateUsed()
                            : data.getExchangeRate()
            );

    BigDecimal usdComponentsBob =
            safeMoney(data.getSubtotalUsd())
                    .multiply(commercialExchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);

    BigDecimal boliviaOperationCost =
            safeMoney(
                    data.getTotalOperationBob() != null
                            ? data.getTotalOperationBob()
                            : data.getTotalBob()
            );

    BigDecimal totalInvestment =
            usdComponentsBob
                    .add(boliviaOperationCost)
                    .setScale(2, RoundingMode.HALF_UP);

    BigDecimal fobUsd =
            safeMoney(data.getFobUsd());

    BigDecimal bankCommissionUsd =
            safeMoney(data.getBankTransferCommissionUsd());

    int payments =
            data.getFobPaymentCount() == null
                    ? 1
                    : Math.max(
                            1,
                            data.getFobPaymentCount()
                    );

    BigDecimal firstFobInstallment =
            fobUsd.divide(
                    BigDecimal.valueOf(payments),
                    2,
                    RoundingMode.HALF_UP
            );

    BigDecimal initialPaymentUsd =
            firstFobInstallment
                    .add(bankCommissionUsd)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

    PdfPTable start = new PdfPTable(2);
    start.setWidthPercentage(100);
    start.setWidths(new float[]{4f, 1.5f});

    start.addCell(
            headerCell(
                    "Pago al confirmar el pedido",
                    whiteFont
            )
    );

    start.addCell(
            headerCell(
                    "Total",
                    whiteFont
            )
    );

    start.addCell(
            valueCell(
                    payments > 1
                            ? "Primera cuota FOB"
                            : "Valor FOB mercadería",
                    textFont
            )
    );

    start.addCell(
            valueCell(
                    "USD " + format(
                            payments > 1
                                    ? firstFobInstallment
                                    : fobUsd
                    ),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    start.addCell(
            valueCell(
                    "Comisión giro bancario",
                    textFont
            )
    );

    start.addCell(
            valueCell(
                    "USD " + format(
                            bankCommissionUsd
                    ),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    PdfPCell initialLabel = valueCell(
            "TOTAL INICIAL",
            labelFont,
            Element.ALIGN_RIGHT
    );

    initialLabel.setBackgroundColor(YELLOW);
    start.addCell(initialLabel);

    PdfPCell initialValue = valueCell(
            "USD " + format(
                    initialPaymentUsd
            ),
            labelFont,
            Element.ALIGN_RIGHT
    );

    initialValue.setBackgroundColor(YELLOW);
    start.addCell(initialValue);

    document.add(start);
    document.add(spacer(10));

if (payments > 1) {
    BigDecimal remainingFobUsd =
            fobUsd.subtract(
                    firstFobInstallment
            );

    PdfPTable beforeShipment =
            new PdfPTable(2);

    beforeShipment.setWidthPercentage(100);
    beforeShipment.setWidths(
            new float[]{4f, 1.5f}
    );

    beforeShipment.addCell(
            headerCell(
                    "Antes del embarque",
                    whiteFont
            )
    );

    beforeShipment.addCell(
            headerCell(
                    "Total",
                    whiteFont
            )
    );

    beforeShipment.addCell(
            valueCell(
                    "Saldo pendiente FOB",
                    textFont
            )
    );

    beforeShipment.addCell(
            valueCell(
                    "USD " + format(
                            remainingFobUsd
                    ),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    document.add(beforeShipment);
    document.add(spacer(10));
}

    PdfPTable investment = new PdfPTable(2);
    investment.setWidthPercentage(100);
    investment.setWidths(new float[]{3f, 2f});

    PdfPCell title = new PdfPCell(new Phrase("INVERSIÓN REFERENCIAL TOTAL", whiteFont));
    title.setColspan(2);
    title.setBackgroundColor(NAVY);
    title.setHorizontalAlignment(Element.ALIGN_CENTER);
    title.setPadding(8);
    investment.addCell(title);

    investment.addCell(
            valueCell(
                    "Componentes en dólares convertidos a Bs",
                    textFont
            )
    );

    investment.addCell(
            valueCell(
                    "Bs " + format(usdComponentsBob),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    investment.addCell(
            valueCell(
                    "Costos de operación en Bolivia",
                    textFont
            )
    );

    investment.addCell(
            valueCell(
                    "Bs " + format(
                            boliviaOperationCost
                    ),
                    labelFont,
                    Element.ALIGN_RIGHT
            )
    );

    PdfPCell totalLabel = new PdfPCell(
            new Phrase(
                    "INVERSIÓN REFERENCIAL TOTAL",
                    whiteFont
            )
    );
    totalLabel.setBackgroundColor(ORANGE);
    totalLabel.setPadding(12);
    investment.addCell(totalLabel);

    Font totalInvestmentFont = new Font(Font.HELVETICA, 14, Font.BOLD, NAVY);

    PdfPCell totalValue = new PdfPCell(
            new Phrase("Bs " + format(totalInvestment), totalInvestmentFont)
    );
    totalValue.setBackgroundColor(YELLOW);
    totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    totalValue.setPadding(12);
    investment.addCell(totalValue);

    document.add(investment);
    document.add(spacer(10));

PdfPTable later = new PdfPTable(2);
later.setWidthPercentage(100);
later.setWidths(new float[]{4f, 1.5f});

later.addCell(
        headerCell(
                "Pagos posteriores",
                whiteFont
        )
);

later.addCell(
        headerCell(
                "Total",
                whiteFont
        )
);

later.addCell(
        valueCell(
                "Transporte marítimo - pagar 5 días antes de la llegada al puerto destino",
                textFont
        )
);

later.addCell(
        valueCell(
                "USD " + format(
                        data.getMaritimeFreightUsd()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Liberación de contenedor - pagar según instrucción operativa",
                textFont
        )
);

later.addCell(
        valueCell(
                "USD " + format(
                        data.getContainerReleaseUsd()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Transporte terrestre - pagar en Bolivia, variable según disponibilidad de unidades",
                textFont
        )
);

later.addCell(
        valueCell(
                "Bs " + format(
                        data.getInlandFreightBob()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Otros gastos operativos",
                textFont
        )
);

later.addCell(
        valueCell(
                "Bs " + format(
                        data.getMiscellaneousExpensesBob()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Impuestos a la Aduana Nacional - pagar cuando la carga llegue a Aduana",
                textFont
        )
);

later.addCell(
        valueCell(
                "Bs " + format(
                        data.getCustomsTaxesBob()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Comisión de la agencia despachante de aduana",
                textFont
        )
);

later.addCell(
        valueCell(
                "Bs " + format(
                        data.getDispatchAgentCommissionBob()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

later.addCell(
        valueCell(
                "Comisión Genuino Importaciones",
                textFont
        )
);

later.addCell(
        valueCell(
                "Bs " + format(
                        data.getGenuinoCommissionBob()
                ),
                labelFont,
                Element.ALIGN_RIGHT
        )
);

document.add(later);
document.add(spacer(12));

    Paragraph footer = new Paragraph(
            "Documento generado por Genuino CRM. Proforma sujeta a variación de tipo de cambio, aduana, transporte y disponibilidad operativa.",
            smallFont
    );
    footer.setAlignment(Element.ALIGN_CENTER);
    document.add(footer);
}

private BigDecimal safeMoney(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
}

private void addAttachmentsSection(
        Document document,
        java.util.List<ProformaAttachment> attachments,
        Font whiteFont,
        Font labelFont,
        Font textFont
) throws DocumentException {

    if (attachments == null || attachments.isEmpty()) {
        return;
    }

    addSectionTitle(document, "PRODUCTO Y PROVEEDOR", whiteFont, NAVY);

    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);

    for (ProformaAttachment attachment : attachments) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT);

        String title = attachment.getTitle() != null && !attachment.getTitle().isBlank()
                ? attachment.getTitle()
                : "Link de referencia";

        cell.addElement(new Paragraph(title, labelFont));

        boolean isImage =
                "PRODUCT_IMAGE".equalsIgnoreCase(attachment.getAttachmentType())
                || "SUPPLIER_IMAGE".equalsIgnoreCase(attachment.getAttachmentType());

        if (isImage && attachment.getAttachmentUrl() != null && !attachment.getAttachmentUrl().isBlank()) {
            try {
                String relativePath = attachment.getAttachmentUrl().replaceFirst("^/uploads/", "uploads/");
                Image image = Image.getInstance(relativePath);
                image.scaleToFit(140, 140);
                image.setSpacingBefore(6);
                image.setSpacingAfter(6);
                cell.addElement(image);
            } catch (Exception ex) {
                cell.addElement(new Paragraph("Imagen no disponible: " + attachment.getAttachmentUrl(), textFont));
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