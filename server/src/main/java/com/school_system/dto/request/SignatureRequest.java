package com.school_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureRequest {
    @NotNull(message = "Unterrichts-ID darf nicht leer sein.")
    private Long lessonId;
    @NotBlank(message = "Unterschrift darf nicht leer sein.")
    private String signature;


}
