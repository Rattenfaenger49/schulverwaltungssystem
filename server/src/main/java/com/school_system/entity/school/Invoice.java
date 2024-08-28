package com.school_system.entity.school;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.security.User;
import com.school_system.enums.school.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal adjustments;
    private BigDecimal discountAmount;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    private LocalDate payment_date;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private FileMetadata file;
    private String paymentMethod;
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Signature signature;
    private String updatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;


}

