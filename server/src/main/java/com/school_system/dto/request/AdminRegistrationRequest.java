package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.school.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminRegistrationRequest {

    @NotBlank(message = "Vorname ist Pflichtfeld")
    private String firstName;

    @NotBlank(message = "Nachname ist Pflichtfeld")
    private String lastName;

    @NotBlank(message = "Email ist Pflichtfeld")
    private String email;


    @NotBlank(message = "Telefonnummer ist Pflichtfeld")
    private String phoneNumber;

    @Valid
    @NotNull(message = "Adresse ist Pflichtfeld")
    private Address address;

}
