package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstitutionRequest {

    private long id;
    @NotBlank(message = " Name ist Pflichtfeld")
    private String name;

    @NotBlank(message = "Email ist Pflichtfeld")
    private String email;

    @NotBlank(message = "Telefonnummer ist Pflichtfeld")
    private String phoneNumber;

    @Valid
    @NotNull(message = "Adresse ist Pflichtfeld")
    private Address address;

    @NotBlank(message = "Kontakte ist Pflichtfeld")
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

}
