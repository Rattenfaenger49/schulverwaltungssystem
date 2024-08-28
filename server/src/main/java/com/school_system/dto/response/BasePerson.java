package com.school_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasePerson {
    private Long id;
    private  String firstName;
    private  String lastName;
    private  String username;
}
