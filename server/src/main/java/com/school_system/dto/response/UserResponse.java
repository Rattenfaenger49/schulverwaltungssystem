package com.school_system.dto.response;

import com.school_system.entity.school.Address;
import com.school_system.enums.school.Gender;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private Gender gender;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String phoneNumber;
    private String username;
    private String comment;
    private Address address;
    private boolean verified;
    private Boolean markedForDeletion;

}
