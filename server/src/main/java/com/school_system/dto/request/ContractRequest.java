package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.school.Modul;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractRequest {

    private Long id;

    @NotBlank(message = "Vertragsnummer ist Pflichtfeld")
    private String contractNumber;

    private String referenceContractNumber;


    @NotBlank(message = "Vorname ist Pflichtfeld")
    private ContractType contractType;

    @NotBlank(message = "Nachname ist Pflichtfeld")
    private ContractStatus status;

    @NotNull(message = "Vertragsbeginn ist Pflichtfeld")
    private LocalDate startAt;

    @NotNull(message = "Vertragsende ist Pflichtfeld")
    private LocalDate endAt;
    @Builder.Default
    private List<Modul> moduls = new ArrayList<>();

    @NotNull
    private BasicUser student;

    @Valid
    private ContactRequest contact;

    private String comment;



}
