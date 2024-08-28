package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherRegistrationRequest extends UserRegistrationRequest {

    private String education;
    private String qualifications;
    @NotNull(message = "Studenlohn ist Pflichtfeld")
    private BigDecimal singleLessonCost;
    @NotNull(message = "Studenlohn für Gruppen ist Pflichtfeld")
    private BigDecimal groupLessonCost;
    @NotNull(message = "Steuer-Id ist Pflichtfeld")
    private String taxId;
}
