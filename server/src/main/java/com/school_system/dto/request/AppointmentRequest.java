package com.school_system.dto.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentRequest {
    private Long id;
    @NotNull(message = "Geschlecht ist Pflichtfeld")
    private String title;
    private String content;
    @NotNull(message = "Startdatum ist Pflichtfeld")
    private LocalDateTime startAt;
    @NotNull(message = "Enddatum ist Pflichtfeld")
    private LocalDateTime endAt;
    private ContractType contractType;
    private String status;



}
