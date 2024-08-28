package com.school_system.common;

import com.school_system.entity.school.Modul;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponse {
    private boolean isValid;
    private String message;
    private Modul modul;
}
