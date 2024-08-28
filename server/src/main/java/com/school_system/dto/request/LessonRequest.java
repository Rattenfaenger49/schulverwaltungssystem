package com.school_system.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.LessonType;
import com.school_system.enums.school.ModulType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonRequest {

    private Long id;
    @NotNull(message = "Moduletype ist Pflichtfeld")
    private ModulType modulType;

    @NotNull(message = "Datum ist Pflichtfeld")
    private LocalDateTime startAt;

    @NotNull(message = "Unterrichtseinheiten ist Pflichtfeld")
    private Double units;

    @NotNull(message = "Unterrichtsform ist Pflichtfeld")
    private LessonType lessonType;
    @NotNull(message = "Vertragsart ist Pflichtfeld")
    private ContractType contractType;

    @NotBlank(message = "DESCRIPTION ist Pflichtfeld")
    private String description;
    
    private String comment;
    @NotNull(message = "Lehrer/in ist Pflichtfeld")
    private Teacher teacher;
    @Builder.Default
    private List<StudentResponse> students = new ArrayList<>();


}
