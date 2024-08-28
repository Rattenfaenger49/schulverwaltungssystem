package com.school_system.dto.response;

import com.school_system.entity.school.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Address address;
    @Builder.Default
    private List<ContactResponse> contacts = new ArrayList<>();

}
