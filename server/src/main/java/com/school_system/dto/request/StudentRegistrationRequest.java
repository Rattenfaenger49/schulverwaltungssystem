package com.school_system.dto.request;

import com.school_system.entity.school.Parent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationRequest extends UserRegistrationRequest{

    @NotBlank(message = "Stufe ist Pflichtfeld")
    private String level;
    @NotNull(message = "Portal-Access ist Pflichtfeld")
    private boolean portalAccess;

    private Parent parent;
}
