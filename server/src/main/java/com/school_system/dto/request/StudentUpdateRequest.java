package com.school_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school_system.entity.school.Contract;
import com.school_system.entity.school.Parent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class StudentUpdateRequest extends UserUpdateRequest{

    @NotBlank(message = "Stufe ist Pflichtfeld")
    private String level;
    @Builder.Default
    private List<Contract> contracts = new ArrayList<>();
    @Valid
    private ParentRequest parent;
}








