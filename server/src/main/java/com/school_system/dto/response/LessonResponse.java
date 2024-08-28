package com.school_system.dto.response;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.school.FileMetadata;
import com.school_system.entity.school.StudentLesson;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.LessonType;
import com.school_system.enums.school.ModulType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {

    private Long id;
    private UserFullNameResponse teacher;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startAt;
    @Builder.Default
    private List<StudentLessonResponse> studentsLesson = new ArrayList<>();
    private String description;
    private String comment;
    private ModulType modulType;
    private LessonType lessonType;
    private ContractType contractType;
    private Double units;
    private String isSigned;
    @Builder.Default
    private List<FileMetadata> files = new ArrayList<>();

}
