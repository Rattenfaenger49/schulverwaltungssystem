package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.config.Databases.TenantContext;
import com.school_system.dto.request.FileMetaDataRequest;
import com.school_system.dto.response.InvoiceResponse;
import com.school_system.entity.school.FileMetadata;
import com.school_system.entity.school.Invoice;
import com.school_system.entity.school.Signature;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.FileCategory;
import com.school_system.enums.school.InvoiceStatus;
import com.school_system.enums.school.StorageType;
import com.school_system.repository.FileMetaDataRepository;
import com.school_system.repository.InvoiceRepository;
import com.school_system.service.FileService;
import com.school_system.service.SecurityService;
import com.school_system.service.StorageService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.school_system.mapper.Mapper.toInvoiceResponse;
import static com.school_system.util.InvoiceUtils.generateInvoiceNumber;


@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {


    private final FileMetaDataRepository fileMetaDataRepository;
    private final StorageService storageService;
    private final PDFCreaterServiceImpl customPDFCreater;
    private final HttpServletResponse httpServletResponse;
    private final InvoiceRepository invoiceRepository;
    private final SecurityService securityService;


    @Override
    public ResponseObject<byte[]> getFile(Long fileId) {
        FileMetadata fileMetadata = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File nicht gefunden!"));
        byte[] file = storageService.load(fileMetadata.getFullPath());

        return ResponseObject.<byte[]>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Datei erfolgreich geladen!")
                .data(file)
                .build();

    }

    @Override
    @Transactional
    public ResponseObject<?> uploadFile(MultipartFile file, FileMetaDataRequest metadata) {


        String apiPath = metadata.fileCategory().getValue() + "/" + metadata.id();
        //  tenantId  + path where to save the file
        String fullPath = TenantContext.getCurrentTenant() + "/" + apiPath;

        storageService.store(file, fullPath);

        FileMetadata fileMetadata = FileMetadata.builder()
                .fileName(getFileNameWithoutExtension(file.getOriginalFilename()))
                .size(file.getSize())
                .fileType(file.getContentType())
                .entityId(metadata.id())
                .fullPath(fullPath + "/" + file.getOriginalFilename())
                // for other SotrageType
                .storageType(StorageType.LOCAL)
                .fileCategory(metadata.fileCategory())
                .uploadedBy(UserUtils.getUsername(SecurityContextHolder.getContext().getAuthentication()))
                .uploadedAt(LocalDateTime.now())
                .build();

        if (metadata.fileCategory().equals(FileCategory.INVOICE)) {
            // Invoicenumber
            String invoiceNumber = String.format("%02d-%04d-%02d", metadata.id(), 1, LocalDate.now().getYear() % 100);
            Optional<Invoice> optionalInvoice = invoiceRepository.findFirstByUserIdOrderByInvoiceDateDescIdDesc(metadata.id());
            if (optionalInvoice.isPresent()) {
                invoiceNumber = generateInvoiceNumber(optionalInvoice.get());
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = new Teacher();
            teacher.setId(metadata.id());
            LocalDateTime date = LocalDateTime.now();
            Invoice invoice = Invoice.builder()
                    .taxes(BigDecimal.ZERO)
                    .notes("uplaoded pdf")
                    .user(teacher)
                    .invoiceDate(LocalDate.now())
                    .periodStartDate(LocalDate.now())
                    .periodEndDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(30))
                    .discountAmount(BigDecimal.ZERO)
                    .adjustments(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .invoiceStatus(InvoiceStatus.UNDER_REVIEW)
                    .file(fileMetadata)
                    .invoiceNumber(invoiceNumber)
                    .payment_date(null)
                    .paymentMethod(null)
                    .subtotal(BigDecimal.ZERO)
                    .updatedAt(date)
                    .createdAt(date)
                    .updatedBy(UserUtils.getUsername(authentication))
                    .createdBy(UserUtils.getUsername(authentication))
                    .build();
            invoiceRepository.save(invoice);
            return ResponseObject.<InvoiceResponse>builder()
                    .message("Die Datei wurde erfolgreich hochgeladen." + file.getOriginalFilename() + "!")
                    .data(toInvoiceResponse(invoice))
                    .build();
        }

        return ResponseObject.<FileMetadata>builder()
                .message("Die Datei wurde erfolgreich hochgeladen." + file.getOriginalFilename() + "!")
                .data(fileMetaDataRepository.save(fileMetadata))
                .build();
    }

    private String getFileNameWithoutExtension(String originalFilename) {
        String filenameWithoutExtension;

        int lastIndex = originalFilename.lastIndexOf('.');
        if (lastIndex != -1) {
            filenameWithoutExtension = originalFilename.substring(0, lastIndex);
        } else {
            // No extension found, use the entire original filename
            filenameWithoutExtension = originalFilename;
        }
        return filenameWithoutExtension;
    }

    @Override
    public ResponseObject<String> deleteFile(Long fileId) throws IOException {
        FileMetadata fileMetadata = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Datei nicht gefunden!"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();
        boolean isStudent = securityService.isStudent();
        Long userId = UserUtils.getUserId(authentication);
        boolean sameUser = false;
        String[] parts = fileMetadata.getFullPath().split("/");
        // TODO check for out of index error
        if (parts.length > 2) {
            Long ownerId = Long.valueOf(parts[2]);
            sameUser = ownerId.equals(userId);
        }
        if ((fileMetadata.getFileCategory() == FileCategory.INVOICE && !isAdmin) ||
                (fileMetadata.getFileCategory() == FileCategory.PERSONAL_FILE && !isAdmin && !sameUser) ||
                (fileMetadata.getFileCategory() == FileCategory.HOMEWORK && !isTeacher && !isAdmin)) {
            throw new AccessDeniedException("Sie können diese Datei nicht löschen");
        }

        fileMetaDataRepository.delete(fileMetadata);

        boolean isDeleted = storageService.deleteFile(fileMetadata.getFullPath());

        if (!isDeleted) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Datei wurde nicht gefunden. Aber Datei-Info wurden gelöscht!")
                    .build();
        }
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Datei erfolgreich gelöscht!")
                .data("")
                .build();
    }

    @Override
    @Transactional
    public byte[] addSignatureToInvoice(Invoice invoice, Signature signature) {
        FileMetadata fileMetadata = fileMetaDataRepository.findByFileName(invoice.getInvoiceNumber() + ".pdf")
                .orElseThrow(() -> new EntityNotFoundException("Etwas ist schiefgelaufen. Datei nicht gefunden!"));
        byte[] invoicePdf = storageService.load(fileMetadata.getFullPath());
        invoicePdf = customPDFCreater.addSignatureToInvoice(invoicePdf, signature);

        storageService.store(invoicePdf, fileMetadata);

        return invoicePdf;
    }


}
