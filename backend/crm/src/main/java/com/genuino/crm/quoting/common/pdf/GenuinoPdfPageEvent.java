package com.genuino.crm.quoting.common.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;

import java.awt.Color;

public class GenuinoPdfPageEvent
        extends PdfPageEventHelper {

    @Override
    public void onEndPage(
            PdfWriter writer,
            Document document
    ) {

        PdfContentByte canvas =
                writer.getDirectContentUnder();

        float width =
                document.getPageSize()
                        .getWidth();

        float height =
                document.getPageSize()
                        .getHeight();

        /*
         * Fondo blanco
         */
        canvas.saveState();

        canvas.setColorFill(
                new Color(
                        255,
                        255,
                        255
                )
        );

        canvas.rectangle(
                0,
                0,
                width,
                height
        );

        canvas.fill();

        /*
         * Marca de agua circular inferior derecha
         */
        canvas.setColorStroke(
                new Color(
                        244,
                        244,
                        247
                )
        );

        canvas.setLineWidth(20);

        canvas.circle(
                width + 25,
                220,
                115
        );

        canvas.stroke();

        canvas.circle(
                width + 25,
                220,
                75
        );

        canvas.stroke();

        /*
         * Curva corporativa inferior
         */
        canvas.setColorFill(
                GenuinoPdfBranding.PURPLE_DARK
        );

        canvas.moveTo(
                0,
                0
        );

        canvas.lineTo(
                width,
                0
        );

        canvas.lineTo(
                width,
                52
        );

        canvas.curveTo(
                width * 0.70f,
                25,
                width * 0.25f,
                34,
                0,
                78
        );

        canvas.closePath();
        canvas.fill();

        /*
         * Línea naranja inferior
         */
        canvas.setColorStroke(
                GenuinoPdfBranding.ORANGE
        );

        canvas.setLineWidth(3);

        canvas.moveTo(
                0,
                79
        );

        canvas.curveTo(
                width * 0.25f,
                35,
                width * 0.70f,
                26,
                width,
                54
        );

        canvas.stroke();

        canvas.restoreState();
    }
}