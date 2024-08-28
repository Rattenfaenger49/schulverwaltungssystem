package com.school_system.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.school_system.classes.BillItem;
import com.school_system.entity.school.*;
import com.school_system.common.ResponseObject;
import com.school_system.config.FontManager;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.exception.TenantException;
import com.school_system.init.ProfilesLoader;
import com.school_system.service.PDFCreaterService;
import com.school_system.util.PdfTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.school_system.util.FormatUtils.formatNumber;
import static com.school_system.util.PdfTools.createCell;


@Slf4j
@Service
@RequiredArgsConstructor
public class PDFCreaterServiceImpl implements PDFCreaterService {

    private final FontManager fontManager;



    @Override
    public ResponseObject<byte[]> createDocumentation(List<Lesson> lessons, LocalDate start, LocalDate end, boolean isStudent, ClientInfo clientInfo) {
        try {
            double totalUnits = lessons.parallelStream().mapToDouble(Lesson::getUnits).sum();
            int totalLessons = lessons.size();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'Uhr'", Locale.GERMANY);
            String userFullname = isStudent ? lessons.get(0).getStudentLessons().get(0).getStudent().getFullName() : lessons.get(0).getTeacher().getFullName();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Initialize iText PDF document
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterEventHandler(document, clientInfo ));
            document.setMargins(72, 36, 10, 36);

            // add Title to the document
            document.add(new Paragraph("Dokumentation für: " + userFullname).setFontSize(18).setBold());
            document.add(new Paragraph("Von: " + start + " Bis: " + end + " - Unterrichte: " + totalLessons + " - UE: " + totalUnits).setFontSize(12).setBold());

            // available space for the document is 760 - 60f for footer and header
            // avilable space for ocntent is availableSpace = 760 - 60f = 700
            double PAGE_SPACE = 14;
            double availableSpace = PAGE_SPACE - 1;  // remove the space for the title just in first Page

            for (Lesson lesson : lessons) {
                double removedSpace = 0;
                // init signature
                float[] colums = {30, 20, 50};
                Table tablteFirstRow = new Table(UnitValue.createPercentArray(colums)).useAllAvailableWidth();
                tablteFirstRow.addCell(new Paragraph()
                        .add(new Text("Fach: ").setBold())
                        .add(lesson.getModulType().toString()));
                tablteFirstRow.addCell(new Paragraph(new Text("Stunden: ").setBold())
                        .add(String.valueOf(lesson.getUnits())));
                tablteFirstRow.addCell(new Paragraph()
                        .add(new Text("Datum: ").setBold())
                        .add(lesson.getStartAt().format(formatter)));
                availableSpace -= 0.5;
                removedSpace += 0.5;
                colums = new float[]{30, 70};
                Table tableSecondRow = new Table(UnitValue.createPercentArray(colums)).useAllAvailableWidth();
                Cell cell = new Cell()
                        .add(new Paragraph(new Text("DESCRIPTION: ").setBold()));
                tableSecondRow.addCell(cell);
                tableSecondRow.addCell(new Paragraph(lesson.getDescription().trim()));
                // remove the space for the description
                var decsHeight = calculateTableHeight(lesson.getDescription().trim());
                availableSpace -= decsHeight;
                removedSpace += decsHeight;

                colums = new float[]{50, 50};
                Table tablteThirdRow = new Table(UnitValue.createPercentArray(colums)).useAllAvailableWidth();

                tablteThirdRow.addCell(new Paragraph(new Text(isStudent ? "Lehrer/in: " : "Schüler/in: ").setBold())
                        .add(isStudent ? lesson.getTeacher() != null ? lesson.getTeacher().getFullName() : "Nicht vorhanden" : lesson.getStudentLessons().stream().map(sl -> sl.getStudent().getFullName()).collect(Collectors.joining(", "))));
                cell = new Cell()
                        .add(new Paragraph(new Text("Unterschrift: ").setBold()));
                tablteThirdRow.addCell(cell);
                Optional<byte[]> signatureData = lesson.getSignature().stream().findFirst().map(Signature::getSignature);
                availableSpace -= 0.5;
                removedSpace += 0.5;
                if (signatureData.isPresent()) {
                    // add the deleted space for Signature Title
                    Image image = new Image(ImageDataFactory.create(signatureData.get()));
                    float width = 113.55f; // Set desired width
                    float height = 40; // Set desired height
                    image.scaleToFit(width, height);
                    cell.add(image).setVerticalAlignment(VerticalAlignment.MIDDLE);
                    if (lesson.getStudentLessons().size() > 3) {
                        availableSpace -= ((lesson.getStudentLessons().size() - 1) * 0.4);
                        removedSpace += ((lesson.getStudentLessons().size() - 1) * 0.4);
                    } else {
                        // remove the space for the signature and header
                        availableSpace -= 0.8;
                        removedSpace += 0.8;
                    }

                }

                if (availableSpace < 0) {
                    document.add(new AreaBreak());
                    document.add(new Paragraph("\n"));
                    availableSpace = PAGE_SPACE;
                    availableSpace -= removedSpace;
                } else {
                    availableSpace -= 0.6;
                    removedSpace += 0.6;
                }

                document.add(tablteFirstRow);
                document.add(tableSecondRow);
                document.add(tablteThirdRow);
                document.add(new Paragraph("\n"));
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            return ResponseObject.<byte[]>builder().status(ResponseObject.ResponseStatus.SUCCESSFUL).data(pdfBytes).build();
        } catch (Exception e) {
            log.error("Fehler beim Erstellen des PDF-Dokuments: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public byte[] createInvoiceForTeacher(Teacher teacher, List<BillItem> billItems, LocalDate start, LocalDate end, String invoiceNumber, BankData bankData, boolean withTax,ClientInfo clientInfo) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Initialize iText PDF document

            PdfFont regular = fontManager.getRegularFont();
            PdfFont bold = fontManager.getBoldFont();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            Document document = new Document(pdfDoc);
            document.setMargins(10, 20, 0, 20);

            // Load fonts with embedding strategy
            document.setFontSize(10);
            document.setFont(fontManager.getRegularFont());

            document.add(new Paragraph("RECHNUNG").setTextAlignment(TextAlignment.RIGHT).setFontSize(16).setBold());
            PdfTools.drawLine(document, 0, 800, 600, 800, new DeviceRgb(2, 126, 196), 2, 1);
            PdfTools.newLine(document, 2, 14);
            LocalDate now = LocalDate.now();
            // Right-aligned information
            Paragraph contact = new Paragraph()
                    .setFontSize(10)
                    .setMargin(0f)
                    .setPadding(0)
                    .setSpacingRatio(0)
                    .setFixedLeading(14)
                    .addTabStops(new TabStop(600, TabAlignment.RIGHT))
                    // 1. row left
                    .add(clientInfo.getCompanyName())
                    .add("\n")
                    // 2. row left
                    .add(clientInfo.getFullName())
                    .add("\n")
                    // 3. row left
                    .add(clientInfo.getAddress().getStreet() + " " + clientInfo.getAddress().getStreetNumber())
                    .add("\n")  // Neue Zeile
                    // 4. row left
                    .add(clientInfo.getAddress().getPostal() + " " + clientInfo.getAddress().getCity())
                    .add(new Tab())
                    // 1. row right
                    .add("Rechnungsdatum: " + now);

            document.add(contact);
            PdfTools.newLine(document, 1, 14);
            document.add(new Paragraph("RECHNUNG NR. " + invoiceNumber).setFont(bold).setFontSize(14).setFixedLeading(5));
            document.add(new Paragraph(String.format("Rechnungszeitraum: %s-%s", start, end)).setFontSize(9).setFixedLeading(9));
            PdfTools.newLine(document, 1, 14);
            document.add(new Paragraph("Sehr geehrte Damen und Herren,\n"));
            document.add(new Paragraph().setFixedLeading(14)
                    .add("vielen Dank für den erteilten Auftrag und Ihr Vertrauen.\n")
                    .add("Hiremit stelle ich Ihnen meine geleisteten Arbeitsstunden als nachhilfelehrer:"));
            float[] colums = {8, 50, 10, 17, 15};
            Table table = new Table(UnitValue.createPercentArray(colums)).useAllAvailableWidth();

            table.addCell(createCell("Pos.").setFontColor(ColorConstants.WHITE).setFont(bold).setBackgroundColor(new DeviceRgb(2, 126, 196)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("DESCRIPTION").setFontColor(ColorConstants.WHITE).setFont(bold).setBackgroundColor(new DeviceRgb(2, 126, 196)));


            table.addCell(createCell("Menge").setFont(bold).setFontColor(ColorConstants.WHITE).setBackgroundColor(new DeviceRgb(2, 126, 196)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("Einzelpreis").setFont(bold).setBackgroundColor(new DeviceRgb(191, 191, 191)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("Gesamtpreis").setFont(bold).setBackgroundColor(new DeviceRgb(191, 191, 191)).setTextAlignment(TextAlignment.CENTER));


            double summNetto = 0.0;
            // start content of invoice thas to be replaces with user data


            for (int i = 0; i < billItems.size(); i++) {
                BillItem item = billItems.get(i);
                table.addCell(createCell(String.valueOf(i + 1)).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

                table.addCell(createCell(item.description()));

                table.addCell(createCell(String.valueOf(item.amount())).setTextAlignment(TextAlignment.CENTER));

                table.addCell(createCell(formatNumber(item.cost().doubleValue())).setTextAlignment(TextAlignment.CENTER));
                double product = item.cost().multiply(BigDecimal.valueOf(item.amount())).doubleValue();
                summNetto += product;
                table.addCell(createCell(formatNumber(product))

                        .setTextAlignment(TextAlignment.RIGHT));
            }

            for (var i = 0; i < 3; i++) {
                table.addCell(createCell("").setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)));
            }
            table.addCell(createCell(new Paragraph("Summe Netto")).setFont(bold).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(new Paragraph(formatNumber(summNetto)).setFont(bold)).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setTextAlignment(TextAlignment.RIGHT));


            for (var i = 0; i < 3; i++) {
                table.addCell(createCell(""));
            }

            table.addCell(createCell(new Paragraph("zzgl. UST. 19%")).setTextAlignment(TextAlignment.RIGHT)

            );
            double ust = withTax ? summNetto * 0.19 : 0;
            table.addCell(createCell(new Paragraph(formatNumber(ust))).setTextAlignment(TextAlignment.RIGHT));


            for (var i = 0; i < 3; i++) {
                table.addCell(createCell(""));
            }

            table.addCell(createCell(new Paragraph("Gesamtsumme").setFont(bold).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.RIGHT).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setBackgroundColor(new DeviceRgb(2, 126, 196)));

            table.addCell(createCell(new Paragraph(formatNumber(summNetto + ust)).setFont(bold).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.RIGHT).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setBackgroundColor(new DeviceRgb(2, 126, 196)));

            document.add(table);
            document.add(new Paragraph("\nBitte überweisen Sie den gesamten Betrag innerhalb von 14 Tagen auf folgendes Konto:").setFixedLeading(14));


            document.add(new Paragraph().setFixedLeading(14)
                    .add(String.format("Bank: %s \n", bankData.getBankName()))
                    .add(String.format("Kontoinhaber: %s \n", bankData.getAccountHolderName()))
                    .add(String.format("IBAN: %s \n", bankData.getIban()))
                    .add(String.format("BIC: %s ", bankData.getBic())));
            if (!withTax) {
                document.add(new Paragraph("\nGemäß §19 UStG wird keine Umsatzsteuer berechnet."));
            }
            document.add(new Paragraph().setFixedLeading(14)
                    .add("Bei Rückfragen stehen wir selbstverständlich jederzeit gerne zur Verfügung.\n\n")
                    .add("Mit freundlichne Grüßen\n")
                    .add("Max Muster"));


            // Add custom metadata for signature coordinates
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            // x = 15, y = 100
            float y = 150 - (billItems.size() * 20);

            info.setMoreInfo("signatureX", "15");
            info.setMoreInfo("signatureY", String.valueOf(y));

/*
            // Create a rectangle for the signature box
            Rectangle rect = new Rectangle(15, 85, 120, 50); // x, y, width, height

            // Create a PdfAnnotation representing the signature box
            PdfAnnotation annotation = new PdfWidgetAnnotation(rect)
                    .setFlags(PdfAnnotation.PRINT)
                    .put(PdfName.Subtype, PdfName.Widget)
                    .put(PdfName.Type, PdfName.Annot);
            annotation.setColor(ColorConstants.RED);
            // Add the annotation to the page
            PdfPage page = pdfDoc.getFirstPage();
            page.addAnnotation(annotation);

 */

            // Add Footer
            // Create a table with 3 columns
            // Get the size of the page
            Rectangle pageSize = pdfDoc.getDefaultPageSize();

            // Define the footer content
            String[][] footerContent = {{teacher.getFullName(), "Tel.: " + teacher.getPhoneNumber(), "Bank: " + bankData.getBankName()}, {teacher.getAddress().getStreet() + " " + teacher.getAddress().getStreetNumber(), "E-Mail: " + teacher.getUsername(), "Kontoinhaber: " + bankData.getAccountHolderName()}, {teacher.getAddress().getPostal() + " " + teacher.getAddress().getCity(), "Steuer-Nr: " + " " + teacher.getTaxId(), "IBAN: " + bankData.getIban()}};
            // Create a table for the footer
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{32f, 33f, 35f})).useAllAvailableWidth();
            for (String[] row : footerContent) {
                for (String cellContent : row) {
                    footerTable.addCell(new Cell(

                    )
                            .add(new Paragraph(cellContent).setFixedLeading(10).setFont(regular).setCharacterSpacing(1.1f).setFontSize(8)).setBorder(Border.NO_BORDER));
                }
            }

            // Position the footer at the bottom of the page
            float x = pageSize.getLeft() + document.getLeftMargin();
            y = pageSize.getBottom() - 10;
            float width = pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin();

            // Add footer to the document
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.getFirstPage().newContentStreamBefore(), pdfDoc.getFirstPage().getResources(), pdfDoc);
            Canvas canvas = new Canvas(pdfCanvas, new Rectangle(x, y, width, 60), true);
            canvas.add(footerTable);
            canvas.close();
            PdfTools.drawLine(document, 0, 65, 600, 60, new DeviceRgb(2, 126, 196), 1.6f, 1);

            document.close();

            return baos.toByteArray();


        } catch (Exception e) {
            log.error("Fehler beim Erstellen des PDF-Dokuments: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    private static class HeaderFooterEventHandler implements IEventHandler {
        private final Document document;
        private final ClientInfo clientInfo;

        public HeaderFooterEventHandler(Document document,ClientInfo clientInfo) {
            this.document = document;
            this.clientInfo = clientInfo;

        }

        @Override
        public void handleEvent(Event currentEvent) {

            log.debug("Client Info: {}", clientInfo);
            if (clientInfo == null) {
                throw new TenantException("Mandantendaten nicht gefunden!");
            }
            log.debug("Client Info: {}", clientInfo);
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            Rectangle pageSize = docEvent.getPage().getPageSize();
            PdfFont font = null;
            try {

                font = PdfFontFactory.createFont(); // Use default font
            } catch (IOException e) {
                log.error("Error while creating font: {}", e.getMessage());
                throw new RuntimeException("ERROR: Schriftart nicht gefunden!");
            }
            float footerY = document.getBottomMargin();
            // Add header and footer
            Image logo = null;
            try {
                logo = convertByteArrayToImage(clientInfo.getLogoAsByteArray());
            } catch (IOException ignored) {
                log.warn("convertByteArrayToImage failed");
            }
            // Set logo position and size
            float logoWidth = 126f;
            float logoHeight = 48;
            // pritn logo on the top right corner
            float logoX = pageSize.getRight() - document.getLeftMargin() - logoWidth;
            float logoY = pageSize.getTop() - logoHeight - 15;
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{33.33f, 33.33f, 33.33f})).useAllAvailableWidth();
            // Add content to each cell
            Cell cell1_1 = new Cell()
                    .add(new Paragraph(clientInfo.getFullName())).setBorder(Border.NO_BORDER);
            Cell cell1_2 = new Cell()
                    .add(new Paragraph(clientInfo.getBankData().getIban())).setBorder(Border.NO_BORDER);
            Cell cell1_3 = new Cell()
                    .add(new Paragraph(clientInfo.getBankData().getBankName())).setBorder(Border.NO_BORDER);
            Link chunk = new Link(clientInfo.getWebsite(), PdfAction.createURI(clientInfo.getWebsite()));
            Cell cell2_1 = new Cell()
                    .add(new Paragraph(String.format("Telefon: %s", clientInfo.getPhone()))).setBorder(Border.NO_BORDER);
            Cell cell2_2 = new Cell()
                    .add(new Paragraph(String.format("E-Mail: %s", clientInfo.getEmail()))).setBorder(Border.NO_BORDER);
            Cell cell2_3 = new Cell()
                    .add(new Paragraph("Webseite: ")
                            .add(chunk)).setBorder(Border.NO_BORDER);

            Cell cell3_1 = new Cell()
                    .add(new Paragraph("Adresse:")).setBorder(Border.NO_BORDER);
            Cell cell3_2 = new Cell()
                    .add(new Paragraph(clientInfo.getAddress().getStreet() + clientInfo.getAddress().getStreetNumber())).setBorder(Border.NO_BORDER);
            Cell cell3_3 = new Cell()
                    .add(new Paragraph(clientInfo.getAddress().getPostal() + " " + clientInfo.getAddress().getCity())).setBorder(Border.NO_BORDER);
            // Add cells to the table
            footerTable.addCell(cell1_1);
            footerTable.addCell(cell2_1);
            footerTable.addCell(cell3_1);
            footerTable.addCell(cell1_2);
            footerTable.addCell(cell2_2);
            footerTable.addCell(cell3_2);
            footerTable.addCell(cell1_3);
            footerTable.addCell(cell2_3);
            footerTable.addCell(cell3_3);

            DeviceCmyk magentaColor = new DeviceCmyk();

            new PdfCanvas(docEvent.getPage()).moveTo(pageSize.getLeft(), footerY + 50f).lineTo(pageSize.getRight(), footerY + 50f).setLineWidth(0.7f)  // Adjust the line width as needed
                    .setStrokeColor(magentaColor).stroke();
            Canvas canvas = new Canvas(docEvent.getPage(), pageSize);

            if (logo != null) {
                canvas.add(logo.scaleToFit(logoWidth, logoHeight).setFixedPosition(logoX, logoY));
            }
            canvas

                    .setFont(font).setFontSize(8) // Adjust the font size as needed
                    //.showTextAligned("Header Text", coordX, headerY, TextAlignment.CENTER)
                    //.showTextAligned("Footer Text", coordX, footerY, TextAlignment.CENTER)
                    .add(footerTable.setFixedPosition(pageSize.getLeft() + document.getLeftMargin(), footerY, pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin())).close();
        }

        private Image convertByteArrayToImage(byte[] imageData) throws IOException {
            if (imageData == null) {
                log.warn("Logo resource is null.");
                return null;
            }
            return new Image(ImageDataFactory.create(imageData));

        }
    }


    public double calculateTableHeight(String text) {

        final int CHARACTERS_PER_ROW = 65;
        final double ROW_HEIGHT = 0.3;

        String[] words = text.split(" ");

        int currentLineLength = 0;
        int rowCount = 1;

        for (String word : words) {
            int wordLength = word.length();

            // Check if adding this word exceeds the current line length
            if (currentLineLength + wordLength + 1 > CHARACTERS_PER_ROW) {
                rowCount++;
                currentLineLength = wordLength;
            } else {
                // Add the word to the current line length
                currentLineLength += wordLength + 1; // +1 for the space
            }
        }

        return (rowCount * ROW_HEIGHT) + 0.2;
    }

    @Override
    public byte[] addSignatureToInvoice(byte[] invoicePdf, Signature signature) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfReader reader = new PdfReader(new ByteArrayInputStream(invoicePdf));
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            Document document = new Document(pdfDoc);
     /*
            PdfPage page = pdfDoc.getFirstPage();

            // Assuming the rectangle annotation is the first one on the page
            PdfAnnotation annotation = page.getAnnotations().get(0);
            Rectangle rect = annotation.getRectangle().toRectangle();

            // Now you can get the x and y coordinates
            float xCoordinate = rect.getLeft();
            float yCoordinate = rect.getBottom();

            System.out.println("X coordinate: " + xCoordinate);
            System.out.println("Y coordinate: " + yCoordinate);

            */

            Image signatureImage = new Image(ImageDataFactory.create(signature.getSignature()));
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            float x = Float.parseFloat(info.getMoreInfo("signatureX"));
            float y = Float.parseFloat(info.getMoreInfo("signatureY"));

            signatureImage.setFixedPosition(x, y);
            signatureImage.scaleToFit(113.55f, 40);
            document.add(signatureImage);

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding signature to the PDF", e);
        }
    }

    @Override
    public byte[] createInvoiceToStudent(Student student, List<BillItem> billItems, Contract contract, LocalDate start, LocalDate end, String invoiceNumber, boolean withTax, ClientInfo clientInfo) throws BadRequestException {
        try {
            if(clientInfo == null){
                throw new BadRequestException("Bitte stellen Sie sicher, dass Sie ein Unternehmenprofil angelegt haben!");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Initialize iText PDF document

            PdfFont regular = fontManager.getRegularFont();
            PdfFont bold = fontManager.getBoldFont();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            Document document = new Document(pdfDoc);
            document.setMargins(10, 20, 0, 20);

            // Load fonts with embedding strategy
            document.setFontSize(10);
            document.setFont(regular);

            document.add(new Paragraph("RECHNUNG").setTextAlignment(TextAlignment.RIGHT).setFontSize(16).setBold());
            PdfTools.drawLine(document, 0, 800, 600, 800, new DeviceRgb(2, 126, 196), 2, 1);
            PdfTools.newLine(document, 2, 14);
            LocalDate now = LocalDate.now();
            // Right-aligned information
            Paragraph contact = new Paragraph()
                    .setFontSize(10)
                    .setMargin(0f)
                    .setPadding(0)
                    .setSpacingRatio(0)
                    .setFixedLeading(14)
                    .addTabStops(new TabStop(600, TabAlignment.RIGHT))
                    // 1. left
                    .add(student.getFullName())
                    .add("\n")
                    // 2. left
                    .add(student.getAddress().getStreet() + " " + student.getAddress().getStreet())
                    .add(new Tab())
                    // 2. right
                    .add("Vertragsnummer: " + contract.getContractNumber())
                    .add(new Tab())
                    // 3. left
                    .add(student.getAddress().getPostal() + " " + student.getAddress().getCity())
                    .add(new Tab())
                    // 3. right
                    .add("Rechnungsdatum: " + now)
                    ;
            document.add(contact);

            PdfTools.newLine(document, 1, 16);
            document.add(new Paragraph("RECHNUNG NR. " + invoiceNumber).setFont(bold).setFontSize(14).setFixedLeading(5));
            document.add(new Paragraph(String.format("Rechnungszeitraum: %s-%s", start, end)).setFontSize(9).setFixedLeading(9));
            PdfTools.newLine(document, 1, 14);
            document.add(new Paragraph("Sehr geehrte Damen und Herren,\n"));
            document.add(new Paragraph().setFixedLeading(14)
                    .add("hiermit erhalten Sie die Rechnung für die erbrachten Nachhilfeleistungen für " + student.getFullName() + ".\n")
                    .add("Hiremit stelle ich Ihnen meine geleisteten Arbeitsstunden als nachhilfelehrer:"));
            float[] colums = {8, 50, 10, 17, 15};
            Table table = new Table(UnitValue.createPercentArray(colums)).useAllAvailableWidth();

            table.addCell(createCell("Pos.").setFontColor(ColorConstants.WHITE).setFont(bold).setBackgroundColor(new DeviceRgb(2, 126, 196)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("DESCRIPTION").setFontColor(ColorConstants.WHITE).setFont(bold).setBackgroundColor(new DeviceRgb(2, 126, 196)));


            table.addCell(createCell("Menge").setFont(bold).setFontColor(ColorConstants.WHITE).setBackgroundColor(new DeviceRgb(2, 126, 196)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("Einzelpreis").setFont(bold).setBackgroundColor(new DeviceRgb(191, 191, 191)).setTextAlignment(TextAlignment.CENTER));

            table.addCell(createCell("Gesamtpreis").setFont(bold).setBackgroundColor(new DeviceRgb(191, 191, 191)).setTextAlignment(TextAlignment.CENTER));


            Double summNetto = 0.0;
            // start content of invoice thas to be replaces with user data


            for (int i = 0; i < billItems.size(); i++) {
                BillItem item = billItems.get(i);
                table.addCell(createCell(String.valueOf(i + 1)).setBackgroundColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));

                table.addCell(createCell(item.description())).setFontSize(8);

                table.addCell(createCell(String.valueOf(item.amount())).setTextAlignment(TextAlignment.CENTER));

                table.addCell(createCell(formatNumber(item.cost().doubleValue())).setTextAlignment(TextAlignment.CENTER));
                double product = item.cost().multiply(BigDecimal.valueOf(item.amount())).doubleValue();
                summNetto += product;
                table.addCell(createCell(formatNumber(product))

                        .setTextAlignment(TextAlignment.RIGHT));
            }

            for (var i = 0; i < 3; i++) {
                table.addCell(createCell("").setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)));
            }
            table.addCell(createCell(new Paragraph("Summe Netto")).setFont(bold).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(new Paragraph(formatNumber(summNetto)).setFont(bold)).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setTextAlignment(TextAlignment.RIGHT));


            for (var i = 0; i < 3; i++) {
                table.addCell(createCell(""));
            }

            table.addCell(createCell(new Paragraph("zzgl. UST. 19%")).setTextAlignment(TextAlignment.RIGHT)

            );
            double ust = withTax ? summNetto * 0.19 : 0;
            table.addCell(createCell(new Paragraph(formatNumber(ust))).setTextAlignment(TextAlignment.RIGHT));


            for (var i = 0; i < 3; i++) {
                table.addCell(createCell(""));
            }

            table.addCell(createCell(new Paragraph("Gesamtsumme").setFont(bold).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.RIGHT).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setBackgroundColor(new DeviceRgb(2, 126, 196)));

            table.addCell(createCell(new Paragraph(formatNumber(summNetto + ust)).setFont(bold).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.RIGHT).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.6f)).setBackgroundColor(new DeviceRgb(2, 126, 196)));

            document.add(table);
            document.add(new Paragraph("\nBitte überweisen Sie den gesamten Betrag innerhalb von 14 Tagen.").setFixedLeading(18));

            if (!withTax) {
                document.add(new Paragraph("\nGemäß §19 UStG wird keine Umsatzsteuer berechnet."));
            }

            document.add(new Paragraph().setFixedLeading(14)
                    .add("Bei Rückfragen stehen wir selbstverständlich jederzeit gerne zur Verfügung.\n\n")
                    .add("Mit freundlichne Grüßen\n")
                    .add("Max Muster"));


            // Add custom metadata for signature coordinates
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            // x = 15, y = 100
            // TODO calculate the x and y dynamic for each document
            float y = 150 - (billItems.size() * 20);

            info.setMoreInfo("signatureX", "15");
            info.setMoreInfo("signatureY", String.valueOf(y));

            // Add Footer
            // Create a table with 3 columns
            // Get the size of the page
            Rectangle pageSize = pdfDoc.getDefaultPageSize();

            // Define the footer content
            String[][] footerContent = {{clientInfo.getFullName(), "Tel.: " + clientInfo.getPhone(), "Bank: " +
                    clientInfo.getBankData().getBankName()}, {clientInfo.getAddress().getStreet() + " " +
                    clientInfo.getAddress().getStreetNumber(), "E-Mail: " + clientInfo.getEmail(),
                    "Kontoinhaber: " + clientInfo.getBankData().getAccountHolderName()}, {clientInfo.getAddress().getPostal() + " " + clientInfo.getAddress().getCity(),
                    "Steuer-Nr: " + clientInfo.getTaxNumber(), "IBAN: " + clientInfo.getBankData().getIban()}};
            // Create a table for the footer
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{32f, 33f, 35f})).useAllAvailableWidth();
            for (String[] row : footerContent) {
                for (String cellContent : row) {
                    footerTable.addCell(new Cell(

                    )
                            .add(new Paragraph(cellContent)
                                    .setFixedLeading(10)
                                    .setFont(regular)
                                    .setCharacterSpacing(1.1f)
                                    .setFontSize(8)
                            ).setBorder(Border.NO_BORDER));
                }
            }

            // Position the footer at the bottom of the page
            float x = pageSize.getLeft() + document.getLeftMargin();
            y = pageSize.getBottom() - 10;
            float width = pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin();

            // Add footer to the document
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.getFirstPage().newContentStreamBefore(), pdfDoc.getFirstPage().getResources(), pdfDoc);
            Canvas canvas = new Canvas(pdfCanvas, new Rectangle(x, y, width, 60), true);
            canvas.add(footerTable);
            canvas.close();
            PdfTools.drawLine(document, 0, 65, 600, 60, new DeviceRgb(2, 126, 196), 1.6f, 1);

            document.close();

            return baos.toByteArray();


        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());

        }catch (Exception e) {
            log.error("Fehler beim Erstellen des PDF-Dokuments: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
