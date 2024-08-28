package com.school_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest implements Serializable {
    String oldPassword;
    @NotBlank(message = "Passwort ist Pflichtfeld")
    String password;
    @NotBlank(message = "Bestätigung Passwort ist Pflichtfeld")
    String confirmationPassword;
    @NotBlank(message = "Bestätigungstoken ist Pflichtfeld")
    String confirmationToken;
}
