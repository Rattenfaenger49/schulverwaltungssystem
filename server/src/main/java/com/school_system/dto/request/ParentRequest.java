package com.school_system.dto.request;

import com.school_system.enums.school.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ParentRequest {

    private Long id;

    @NotNull(message = "Geschlecht ist Pflichtfeld")
    private Gender gender;

    @NotBlank(message = "Vorname ist Pflichtfeld!")
    private String firstName;

    @NotBlank(message = "Nachname ist Pflichtfeld!")
    private String lastName;

    @NotBlank(message = "Email ist Pflichtfeld!")
    private String email;

    @NotBlank(message = "Telefonnummer ist Pflichtfeld!")
    private String phoneNumber;

}
