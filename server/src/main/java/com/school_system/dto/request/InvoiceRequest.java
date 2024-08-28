package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.school_system.enums.school.ContractType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceRequest {

    @NotNull(message = "Benutzer-Id int Pflichtfeld!")
    private Long userId;

    @NotNull(message = "Nachname ist Pflichtfeld")
    private LocalDate date;

    @NotNull(message = "Umsatzsteuer ist Pflichtfeld")
    private boolean tax;
    @NotNull(message = "Rechnung speichern ist Pflichtfeld")
    private boolean saveInvoice;

    Long contractId;


}
