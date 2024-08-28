package com.school_system.service;

import com.school_system.classes.BillItem;
import com.school_system.common.ResponseObject;
import com.school_system.entity.school.*;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;


public interface PDFCreaterService {

    byte[] createInvoiceForTeacher(Teacher teacher, List<BillItem> content , LocalDate start, LocalDate end, String invoiceNumber, BankData bankData, boolean withTax, ClientInfo clientInfo);
    ResponseObject<byte[]> createDocumentation(List<Lesson> lessons, LocalDate start, LocalDate end, boolean isStudent,  ClientInfo clientInfo);

    byte[] addSignatureToInvoice(byte[] invoicePdf, Signature signature);

    byte[] createInvoiceToStudent(Student student, List<BillItem> billItems, Contract contract,
                                  LocalDate localDate, LocalDate localDate1, String invoiceNumber,
                                  boolean tax, ClientInfo clientInfo) throws BadRequestException;
}
