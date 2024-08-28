package com.school_system.common;

import com.school_system.entity.school.Modul;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonValidationResponse {
    private boolean isValid;
    private String message;
    private Map<Long,Modul> studentModuls = new HashMap<>();
}
