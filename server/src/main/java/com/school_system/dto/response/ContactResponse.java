package com.school_system.dto.response;


import com.school_system.enums.school.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
    private Long id;
    private Gender gender;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
