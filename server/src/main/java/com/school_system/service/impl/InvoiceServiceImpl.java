package com.school_system.service.impl;


import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import com.school_system.classes.BillItem;
import com.school_system.common.ResponseObject;
import com.school_system.config.Databases.TenantContext;
import com.school_system.dto.request.InvoiceRequest;
import com.school_system.dto.response.FileWithMetadata;
import com.school_system.dto.response.InvoiceResponse;
import com.school_system.entity.school.*;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.entity.security.User;
import com.school_system.enums.school.FileCategory;
import com.school_system.enums.school.InvoiceStatus;
import com.school_system.enums.school.StorageType;
import com.school_system.exception.BankDataRequiredException;
import com.school_system.init.ProfilesLoader;
import com.school_system.mapper.Mapper;
import com.school_system.repository.*;
import com.school_system.service.FileService;
import com.school_system.service.InvoiceService;
import com.school_system.service.PDFCreaterService;
import com.school_system.service.StorageService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.school_system.mapper.Mapper.toInvoiceResponse;
import static com.school_system.util.InvoiceUtils.generateInvoiceNumber;
import static com.school_system.util.InvoiceUtils.generateNextInvoiceNumber;


@Slf4j
@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PDFCreaterService pdfCreaterService;
    private final LessonRepository lessonRepository;
    private final BankDataRepository bankDataRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final SignatureRepository signatureRepository;
    private FileService fileService;
    private StorageService storageService;
    private HttpServletResponse httpServletResponse;
    private ProfilesLoader profilesLoader;

    @Override
    @Cacheable(value = "invoices", key = "#id", condition = "false")
    public ResponseObject<InvoiceResponse> getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Rechnung nicht gefunden")
        );
        return ResponseObject.<InvoiceResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(toInvoiceResponse(invoice))
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<FileWithMetadata> createTeacherInvoice(InvoiceRequest invoiceRequest) throws Exception {

        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if(!clientInfo.getPreferences().getAllowTeacherBillGeneration()){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<FileWithMetadata>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .message("Die Generierung von Rechnungen ist bei Ihnen nicht zugelassen.")
                    .build();
        }
        if(!checkIfInvoiceNeededDataExist(clientInfo)){
            // TODO custom msg for teacher and student
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return ResponseObject.<FileWithMetadata>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .message("Sie müssen erstmal die Bankverbindung und der  Steuer-ID eingeben!")
                    .build();
        }

        Teacher teacher = teacherRepository.findById(invoiceRequest.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Keine Benutzer gefunden.")
        );
        BankData bankData = bankDataRepository.findByUser(teacher).orElseThrow(
                () -> new BankDataRequiredException("Bankdata wurden nicht gefunden!")
        );

        LocalDateTime startOfMonth = invoiceRequest.getDate().atTime(LocalTime.MIN).withDayOfMonth(1);
        LocalDateTime endOfMonth = YearMonth.from(invoiceRequest.getDate()).atEndOfMonth().atTime(LocalTime.MAX);
        LocalDate dateNow = LocalDate.now();

        Optional<Invoice> invoiceOpt = invoiceRepository.findByPeriodStartDateAndUser(startOfMonth.toLocalDate(), teacher);
        if(invoiceOpt.isPresent()){
            log.info("invoice already exists: {}", invoiceOpt.get());
            httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            return ResponseObject.<FileWithMetadata>builder()
                    .message("Eine Rechnung für diesen Monat ist schon vorhanden!")
                    .data(null)
                    .build();
        }


        if(validateInvoiceDate(startOfMonth, endOfMonth, dateNow)){
            endOfMonth = dateNow.atTime(LocalTime.MAX);
        }
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStartAtBetweenOrderByStartAtAsc(invoiceRequest.getUserId(), startOfMonth, endOfMonth);

        double singlStudent = lessons.stream().filter(lesson -> lesson.getStudentLessons().size() == 1).mapToDouble(Lesson::getUnits).sum();
        double multiStudnet = lessons.stream().filter(lesson -> lesson.getStudentLessons().size() > 1).mapToDouble(Lesson::getUnits).sum();


        List<BillItem> billItems = getBillItems(singlStudent, teacher, multiStudnet);

        // Invoicenumber
        String yearMonth = DateTimeFormatter.ofPattern("yyMM").format(startOfMonth);

        // Finde die maximale laufende Nummer für den Benutzer und Monat
        Integer maxNumber = invoiceRepository.findMaxInvoiceNumber(yearMonth, teacher.getId());
        String invoiceNumber = generateNextInvoiceNumber(teacher.getId(), yearMonth,  maxNumber);
        /*
            invoiceNumber = String.format("%02d-%04d-%02d", invoiceRequest.getUserId(), 1, LocalDate.now().getYear() % 100);
            Optional<Invoice> optionalInvoice = invoiceRepository.findFirstByUserIdOrderByInvoiceDateDescIdDesc(invoiceRequest.getUserId());
            if (optionalInvoice.isPresent()) {
                invoiceNumber = generateInvoiceNumber(optionalInvoice.get());
            }
        */


        byte[] file = pdfCreaterService.createInvoiceForTeacher(teacher,
                billItems,
                startOfMonth.toLocalDate(),
                endOfMonth.toLocalDate(),
                invoiceNumber,
                bankData,
                invoiceRequest.isTax(),
                clientInfo);
        // create response Object
      //  signPdf(file);

        FileWithMetadata fileWithMetadata = new FileWithMetadata();
        fileWithMetadata.setFile(file);

        if (invoiceRequest.isSaveInvoice()) {
            Invoice invoice = saveInvoice(file, teacher, invoiceRequest, invoiceNumber, billItems, dateNow, startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
            fileWithMetadata.setInvoice(toInvoiceResponse(invoice));
        }


        return ResponseObject.<FileWithMetadata>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(fileWithMetadata)
                .build();

    }

    private boolean checkIfInvoiceNeededDataExist(ClientInfo clientInfo) {

        return clientInfo != null
                && clientInfo.getBankData() != null
                && clientInfo.getBankData().getBankName() != null
                && clientInfo.getBankData().getIban() != null
                && clientInfo.getBankData().getBic() != null
                && clientInfo.getBankData().getAccountHolderName() != null
                && clientInfo.getTaxNumber() != null;

    }

    private Invoice saveInvoice(byte[] file, User user,
                                InvoiceRequest invoiceRequest, String invoiceNumber,
                                List<BillItem> billItems, LocalDate date, LocalDate startInvoiceDate, LocalDate endInvoiceDate) {

        BigDecimal subTotal = new BigDecimal(0);
        for (BillItem item : billItems) {
            BigDecimal product = item.cost().multiply(BigDecimal.valueOf(item.amount()));
            subTotal = subTotal.add(product);
        }
        BigDecimal taxes = invoiceRequest.isTax() ?
                BigDecimal.valueOf(subTotal.doubleValue() * 0.19) : new BigDecimal(0);
        BigDecimal totalAmount = subTotal.add(taxes);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = UserUtils.getUsername(authentication);
        LocalDateTime dateTime = LocalDateTime.now();
        FileMetadata fileMetadata = FileMetadata.builder()
                .fileName(invoiceNumber + ".pdf")
                .entityId(user.getId())
                .fileCategory(FileCategory.INVOICE)
                .fullPath(TenantContext.getCurrentTenant() + "/" + FileCategory.INVOICE.getValue() + "/" +
                        user.getId() + "/" + invoiceNumber + ".pdf")
                .size((long) file.length)
                .fileType("application/pdf")
                .storageType(StorageType.LOCAL)
                .uploadedAt(dateTime)
                .uploadedBy(userName).build();

        Invoice invoice1 = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(date)
                .periodStartDate(startInvoiceDate)
                .periodEndDate(endInvoiceDate)
                .dueDate(endInvoiceDate.plusDays(15))
                .invoiceStatus(InvoiceStatus.PENDING_APPROVAL)
                .subtotal(subTotal)
                .adjustments(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .taxes(taxes)
                .paymentMethod(null)
                .totalAmount(totalAmount)
                .user(user)
                .file(fileMetaDataRepository.save(fileMetadata))
                .notes("")
                .updatedBy(userName)
                .createdAt(dateTime)
                .createdBy(userName)
                .updatedAt(dateTime).build();
        storageService.store(file, fileMetadata);
        return invoiceRepository.save(invoice1);


    }

    private static List<BillItem> getBillItems(double singlStudent, Teacher teacher, double multiStudnet) {
        List<BillItem> billItems = new ArrayList<>();

        if (singlStudent > 0) {
            BillItem item1 = new BillItem("Einzelunterrichte", singlStudent, teacher.getSingleLessonCost());
            billItems.add(item1);
        }
        if (multiStudnet > 0) {
            BillItem item2 = new BillItem("Gruppenunterrichte", multiStudnet, teacher.getGroupLessonCost());

            billItems.add(item2);
        }
        return billItems;
    }

    private static List<BillItem> getBillItems( List<Lesson> lessons, Contract contract) {
        List<BillItem> billItems = new ArrayList<>();
        for (var modul: contract.getModuls()){

            double sumUnitsSingle = lessons.stream().filter(l -> l.getStudentLessons().size() == 1 &&  l.getStudentLessons().stream().anyMatch(
                    sl -> sl.getModul().equals(modul)
            )).mapToDouble(Lesson::getUnits).sum();
            double sumUnitsGroup = lessons.stream().filter(l -> l.getStudentLessons().size() > 1 && l.getStudentLessons().stream().anyMatch(
                    sl -> sl.getModul().equals(modul)
            )).mapToDouble(Lesson::getUnits).sum();
            if (modul.isSingleLessonAllowed())
            {

                BillItem billItem = new BillItem(String.format("Fach(%s), Einzelunterricht", modul.getModulType().getValue()
                ), sumUnitsSingle, modul.getSingleLessonCost());
                billItems.add(billItem);

            }
            if (modul.isGroupLessonAllowed())
            {
                BillItem billItem = new BillItem(String.format("Fach(%s) Gruppenunterricht",  modul.getModulType().getValue()
                ), sumUnitsGroup, modul.getGroupLessonCost());
                billItems.add(billItem);

            }

        }
        return  billItems.stream().filter(b -> b.amount() != 0).toList();

    }

    @Override
    public ResponseObject<Page<InvoiceResponse>> getInvoicesByUserId(Long userId, Pageable pageable) {
        Page<Invoice> invoice = invoiceRepository.findAllByUserId(userId, pageable);
        return ResponseObject.<Page<InvoiceResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(invoice.map(Mapper::toInvoiceResponse))
                .build();
    }

    @Override
    @Transactional
    @CachePut(value = "invoices", key = "#result.data.id", condition = "false")
    public ResponseObject<InvoiceResponse> updateStatus(Long id, InvoiceStatus invoiceStatus) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Rechnung wurde nicht gefunden!")
        );
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        invoice.setUpdatedBy(UserUtils.getUsername(authentication));
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice.setInvoiceStatus(invoiceStatus);
        invoiceRepository.save(invoice);
        return ResponseObject.<InvoiceResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(toInvoiceResponse(invoice))
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "invoices", key = "#id", condition = "false")
    public ResponseObject<byte[]> signInvoice(Long id, String signautre) {
        String base64Data = signautre.split(",")[1];
        byte[] signatureData = Base64.getDecoder().decode(base64Data);

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Rechnung wurde nicht gefunden!")
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = UserUtils.getUserId(authentication);

        if (!userId.equals(invoice.getUser().getId())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<byte[]>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }


        Signature signature = Signature.builder()
                .signature(signatureData)
                .signedAt(LocalDateTime.now())
                .signedBy(UserUtils.getUsername(SecurityContextHolder.getContext().getAuthentication()))
                .build();
        signatureRepository.save(signature);

        invoice.setSignature(signature);
        invoice.setUpdatedBy(UserUtils.getUsername(authentication));
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice.setInvoiceStatus(InvoiceStatus.UNDER_REVIEW);
        invoiceRepository.save(invoice);

        byte[] res = fileService.addSignatureToInvoice(invoice, signature);

        return ResponseObject.<byte[]>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(res)
                .build();
    }

    @Override
    public ResponseObject<FileWithMetadata> createStudentInvoice(InvoiceRequest invoiceRequest) throws BadRequestException {
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if(!clientInfo.getPreferences().getAllowStudentBillGeneration()){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<FileWithMetadata>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .message("Die Generierung von Rechnungen ist nicht zugelassen.")
                    .build();
        }
        if(!checkIfInvoiceNeededDataExist(clientInfo)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return ResponseObject.<FileWithMetadata>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .message("Sie müssen erstmal die Bankverbindung und der  Steuer-ID eingeben!")
                    .build();
        }
        // TODO maybe check in the request itself
        if(invoiceRequest.getContractId() == null)
            throw  new BadRequestException("Vertrag ist ein Pflichtfeld");

        Student student = studentRepository.findById(invoiceRequest.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Keine Benutzer gefunden.")
        );
        Contract contract = student.getContracts().stream().filter(c ->
                     c.getId().equals(invoiceRequest.getContractId())).findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Vertrag wurde nicht gefunden!")
                );

        LocalDateTime startOfMonth = invoiceRequest.getDate().atTime(LocalTime.MIN).withDayOfMonth(1);
        LocalDateTime endOfMonth = YearMonth.from(invoiceRequest.getDate()).atEndOfMonth().atTime(LocalTime.MAX);

        Optional<Invoice> invoiceOpt = invoiceRepository.findByPeriodStartDateAndUser(startOfMonth.toLocalDate(), student);
        if(invoiceOpt.isPresent()){
            log.info("invoice already exists: {}", invoiceOpt.get());
            httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            return ResponseObject.<FileWithMetadata>builder()
                    .message("Eine Rechnung für diesen Monat ist schon vorhanden!")
                    .data(null)
                    .build();
        }
        LocalDate dateNow = LocalDate.now();

        if(validateInvoiceDate(startOfMonth, endOfMonth, dateNow)){
            endOfMonth = dateNow.atTime(LocalTime.MAX);
        }

        List<Lesson> lessons = lessonRepository.findByStudentsIdAndStartAtBetweenOrderByStartAtAsc(invoiceRequest.getUserId(), startOfMonth, endOfMonth);
        lessons =  lessons.stream()
                .filter(l -> l.getStudentLessons().stream()
                        .anyMatch(sl -> sl.getModul().getContract().equals(contract)))
                .toList();
        List<BillItem> billItems = getBillItems(lessons , contract);

        // Invoicenumber
        String yearMonth = DateTimeFormatter.ofPattern("yyMM").format(startOfMonth);

        // Finde die maximale laufende Nummer für den Benutzer und Monat
        Integer maxNumber = invoiceRepository.findMaxInvoiceNumber(yearMonth, student.getId());
        String invoiceNumber = generateNextInvoiceNumber(student.getId(), yearMonth,  maxNumber);

        /*// Invoicenumber
        String invoiceNumber = String.format("%d-%05d-%02d", invoiceRequest.getUserId(), 1, LocalDate.now().getYear() % 100);
        Optional<Invoice> optionalInvoice = invoiceRepository.findFirstByUserIdOrderByInvoiceDateDescIdDesc(invoiceRequest.getUserId());
        if (optionalInvoice.isPresent()) {
            invoiceNumber = generateInvoiceNumber(optionalInvoice.get());
        }
        */

        byte[] file = pdfCreaterService.createInvoiceToStudent(student, billItems, contract,
                startOfMonth.toLocalDate(), endOfMonth.toLocalDate(), invoiceNumber,
                invoiceRequest.isTax(),
                clientInfo);
        // create response Object

        FileWithMetadata fileWithMetadata = new FileWithMetadata();
        fileWithMetadata.setFile(file);

        if (invoiceRequest.isSaveInvoice()) {
            Invoice invoice = saveInvoice(file, student, invoiceRequest, invoiceNumber, billItems, dateNow,
                    startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
            fileWithMetadata.setInvoice(toInvoiceResponse(invoice));
        }

        return ResponseObject.<FileWithMetadata>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(fileWithMetadata)
                .build();
    }

    private boolean validateInvoiceDate(LocalDateTime startOfMonth, LocalDateTime endOfMonth, LocalDate dateNow) throws BadRequestException {
        // check that the start date of bill is not in the future
        if (dateNow.isBefore(startOfMonth.toLocalDate())) {
            throw new BadRequestException("Rechnungsdatum darf nicht in der Zukunft sein!");
        }
        // insure that the end date of bill is not in the future
        if (dateNow.isBefore(endOfMonth.toLocalDate())) {
            // set it to today if in the feature
            return false;
        }
        return true;
    }

    public  byte[] signPdf(byte[] pdfBytes) throws Exception {
        ByteArrayOutputStream signedPdfOutputStream = new ByteArrayOutputStream();

        // Load the keystore
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream keystoreStream = getClass().getResourceAsStream("certificate.pfx")) {
            ks.load(keystoreStream, "53441".toCharArray());
        }

      // Get the private key and certificate chain
        String alias = ks.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, "53441".toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);

        // Initialize PDF reader and writer
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        PdfWriter writer = new PdfWriter(signedPdfOutputStream);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfSigner signer = new PdfSigner(reader, signedPdfOutputStream, new StampingProperties());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Document signing")        // Reason for signing
                .setLocation("Location")             // Location of signing
                .setPageRect(new Rectangle(15, 150 - (2 * 20), 120, 50)) // Signature field position and size
                .setPageNumber(1);
        // Create the signature
        IExternalSignature pks = new PrivateKeySignature(privateKey, "SHA256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document
        signer.setFieldName("sig");
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

        pdfDoc.close();
        return signedPdfOutputStream.toByteArray();
    }
}

