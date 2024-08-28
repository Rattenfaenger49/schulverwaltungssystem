package com.school_system.dto.response;

import com.school_system.entity.school.Parent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse extends UserResponse {
    @Builder.Default
    private List<ContractResponse> contracts = new ArrayList<>();
    @Builder.Default
    private List<BasePerson> teachers = new ArrayList<>();
    private String level;
    private Parent parent;
}
