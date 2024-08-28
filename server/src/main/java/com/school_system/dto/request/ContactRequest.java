package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.enums.school.Gender;
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
public class ContactRequest {
    private Long id;
    @NotNull(message = "Geschlecht ist Pflichtfeld!")
    private Gender gender;
    @NotNull(message = "Vorname ist Pflichtfeld!")
    private String firstName;
    @NotNull(message = "Nachname ist Pflichtfeld!")
    private String lastName;
    @NotNull(message = "Telefonnummer ist Pflichtfeld!")
    private String phoneNumber;
    @NotNull(message = "Email ist Pflichtfeld!")
    private String email;


}
