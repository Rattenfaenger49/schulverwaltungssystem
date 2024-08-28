package com.school_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLessonResponse {

    private UserFullNameResponse student;
    private long lessonId;
    private ModulResponse modul;
}
