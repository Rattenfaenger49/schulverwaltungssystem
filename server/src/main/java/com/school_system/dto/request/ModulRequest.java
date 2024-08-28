package com.school_system.dto.request;

import com.school_system.dto.response.BaseEntityId;
import com.school_system.enums.school.LessonDuration;
import com.school_system.enums.school.ModulType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModulRequest {


    private Long id;

    @NotNull(message = "Modultype ist Pflichtfeld")
    private ModulType modulType;

    @NotNull(message = "Anzahl der Stunden  ist Pflichtfeld")
    @Min(value = 0, message = "Anzahl der Stunden muss positive Zahl sein")
    private Double units;

    @NotNull(message = "Unterrichtdauer ist Pflichtfeld")
    private LessonDuration lessonDuration;

    @NotNull(message = "Kosten für Einzelunterricht sind erforderlich")
    private BigDecimal singleLessonCost;

    @NotNull(message = "Kosten für Gruppenunterricht sind erforderlich")
    private BigDecimal groupLessonCost;

    @NotNull(message = "Einzelunterricht erlaubt-Flag ist erforderlich")
    private boolean singleLessonAllowed;

    @NotNull(message = "Gruppenunterricht erlaubt-Flag ist erforderlich")
    private boolean groupLessonAllowed;

    private BaseEntityId contract;

}
