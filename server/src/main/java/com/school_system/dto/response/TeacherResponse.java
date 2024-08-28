package com.school_system.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse extends UserResponse {

    private String education;
    private String qualifications;
    private BigDecimal singleLessonCost;
    private BigDecimal groupLessonCost;
    private String taxId;
    @Builder.Default
    private List<StudentResponse> students = new ArrayList<>();

}
