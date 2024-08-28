package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.InvoiceRequest;
import com.school_system.dto.response.FileWithMetadata;
import com.school_system.dto.response.InvoiceResponse;
import com.school_system.enums.school.InvoiceStatus;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface InvoiceService {
    ResponseObject<InvoiceResponse> getInvoice(Long id);
    ResponseObject<FileWithMetadata> createTeacherInvoice(InvoiceRequest invoiceRequest) throws Exception;

    ResponseObject<Page<InvoiceResponse>> getInvoicesByUserId(Long teacherId, Pageable pageable) ;

    ResponseObject<InvoiceResponse> updateStatus(Long id, InvoiceStatus invoiceStatus);
    ResponseObject<byte[]> signInvoice(Long id, String signautre);

    ResponseObject<FileWithMetadata> createStudentInvoice(InvoiceRequest invoiceRequest) throws BadRequestException;
}
