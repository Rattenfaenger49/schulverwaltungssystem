package com.school_system.dto.response;

import com.school_system.enums.school.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContractStudentResponse  {
    private Long id;
    @NotNull(message = "Geschlecht ist Pflichtfeld")
    private Gender gender;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Email(message = "Ungültige Email-Adresse")
    private String username;

    private String level;
}
