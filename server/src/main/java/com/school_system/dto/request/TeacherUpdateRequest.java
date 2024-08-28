package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.security.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherUpdateRequest extends UserUpdateRequest{


    private String education;
    private String qualifications;
    @NotBlank(message = "Schüler/in ist Pflichtfeld")
    @Builder.Default
    private List<Student> students = new ArrayList<>();
    @NotNull(message = "Studenlohn ist Pflichtfeld")
    private BigDecimal singleLessonCost;
    @NotNull(message = "Studenlohn für Gruppen ist Pflichtfeld")
    private BigDecimal groupLessonCost;
    @NotNull(message = "Steuer-Id ist Pflichtfeld")
    private String taxId;

}








