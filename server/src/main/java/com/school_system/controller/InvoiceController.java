package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.InvoiceRequest;
import com.school_system.dto.response.FileWithMetadata;
import com.school_system.dto.response.InvoiceResponse;
import com.school_system.entity.school.Invoice;
import com.school_system.service.InvoiceService;
import lombok.AllArgsConstructor;


import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;


    @GetMapping
    @PreAuthorize("@securityService.isSameUserOrAdmin(#userId)")
    public ResponseObject<Page<InvoiceResponse>> getInvoicesByUserId(@RequestParam Long userId, @PageableDefault(
            sort = "invoiceDate", direction = Sort.Direction.DESC
    ) Pageable pageable){
        return invoiceService.getInvoicesByUserId(userId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseObject<InvoiceResponse> getInvoice(@PathVariable Long id){
        return invoiceService.getInvoice(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<InvoiceResponse> updateStatus(@PathVariable Long id, @RequestBody Invoice invoice){
        return invoiceService.updateStatus(id, invoice.getInvoiceStatus());
    }

    @PutMapping("/{id}/signature")
    public ResponseObject<byte[]> signInvoice(@PathVariable Long id, @RequestBody String signature){
        return invoiceService.signInvoice(id, signature);
    }

    @PostMapping("/teacher")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#invoiceRequest.userId)")
    public ResponseObject<FileWithMetadata> createInvoiceForTeacher(@ RequestBody InvoiceRequest invoiceRequest) throws Exception {
        return invoiceService.createTeacherInvoice(invoiceRequest);
    }
    @PostMapping("/student")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#invoiceRequest.userId)")
    public ResponseObject<FileWithMetadata> createInvoiceForStudent(@ RequestBody InvoiceRequest invoiceRequest) throws BadRequestException {
        return invoiceService.createStudentInvoice(invoiceRequest);
    }

}

