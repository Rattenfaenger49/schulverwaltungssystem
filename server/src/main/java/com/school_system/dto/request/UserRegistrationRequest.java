package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.school.Address;


import com.school_system.enums.school.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationRequest{

    @NotNull(message = "Geschlecht ist Pflichtfeld")
    private Gender gender;

    @NotBlank(message = "Vorname ist Pflichtfeld")
    private String firstName;

    @NotBlank(message = "Nachname ist Pflichtfeld")
    private String lastName;

    @NotNull(message = "Geburtsdatum ist Pflichtfeld")
    private LocalDate birthdate;

    @Email(message = "Ungültige Email-Adresse")
    private String username;

    @NotBlank(message = "Telefonnummer ist Pflichtfeld")
    private String phoneNumber;

    @Valid
    @NotNull(message = "Adresse ist Pflichtfeld")
    private Address address;



}
