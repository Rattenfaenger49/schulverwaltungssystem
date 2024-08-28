package com.school_system.util;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.signatures.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
public class PdfTools {



    public static void  drawLine(Document document,
                                 float x1, float y1, float x2,
                                 float y2, Color color, float width, float opacity){
        // Get the PdfCanvas object for the current page
        PdfCanvas canvas = new PdfCanvas(document.getPdfDocument().getFirstPage());

        // Set line properties (color, width, etc.)
        // ColorConstants.BLACK
        canvas.setStrokeColor(color);
        canvas.setLineWidth(width);
        PdfExtGState gState = new PdfExtGState().setStrokeOpacity(opacity);
        canvas.setExtGState(gState);
        // Draw a line from point (x1, y1) to point (x2, y2)

        canvas.moveTo(x1, y1)
                .lineTo(x2, y2)
                .stroke();
    }

    public static void newLine(Document document, int counter, int space) {
        for (int i = 0; i < counter; i++) {
            document.add(new Paragraph("\n")
                    .setFixedLeading(space));
        }
    }
    public static Cell createCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
    }
    public static Cell createCell(Paragraph p) {
        return new Cell().add(p).setBorder(Border.NO_BORDER);
    }






}
