package com.school_system.dto.response;


import com.school_system.enums.school.LessonDuration;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModulResponse {

    private Long id;

    private ModulType modulType;

    private Double units;
    private LessonDuration lessonDuration;
    private BigDecimal singleLessonCost;
    private BigDecimal groupLessonCost;
    private boolean singleLessonAllowed;
    private boolean groupLessonAllowed;
    private BaseEntityId contract;

}
