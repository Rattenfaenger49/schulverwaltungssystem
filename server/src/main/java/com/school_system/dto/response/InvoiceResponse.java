package com.school_system.dto.response;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.school.Signature;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.InvoiceStatus;
import com.school_system.enums.school.LessonDuration;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private InvoiceStatus invoiceStatus;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate payment_date;
    private Long fileId;
    private String notes;
    private BasePerson teacher;
    private boolean isSigned;

}
