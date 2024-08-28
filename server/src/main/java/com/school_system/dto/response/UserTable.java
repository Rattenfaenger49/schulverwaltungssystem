package com.school_system.dto.response;

import com.school_system.enums.school.Gender;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
}
