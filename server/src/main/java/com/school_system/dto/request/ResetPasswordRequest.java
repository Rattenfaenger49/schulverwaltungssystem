package com.school_system.dto.request;

import com.school_system.entity.school.Address;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link Address}
 */
@Data
public class ResetPasswordRequest implements Serializable {

    @Email(message = "Ungültige Email-Adresse")
    String username;
}
