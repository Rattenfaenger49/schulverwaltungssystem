package com.school_system.entity.school;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "clientInfo")
public class ClientInfo {

    @Id
    private String id;
    @Indexed(unique = true)
    private String tenantId;
    @NotBlank(message = "Schulname ist Pflichtfeld")
    private String companyName;
    @NotBlank(message = "Abkürzung ist Pflichtfeld")
    private String abbreviation;
    @NotBlank(message = "Vorname ist Pflichtfeld")
    private String firstName;
    @NotBlank(message = "Nachname ist Pflichtfeld")
    private String lastName;
    @NotBlank(message = "Email ist Pflichtfeld")
    private String email;
    @NotBlank(message = "Telefonnummer ist Pflichtfeld")
    private String phone;
    @NotBlank(message = "Support Email ist Pflichtfeld")
    private String supportEmail;
    @NotBlank(message = "Support Telefonnummer ist Pflichtfeld")
    private String supportPhone;
    private Address address;
    private BankData bankData;
    @Pattern(regexp = "https?://.+", message = "Ungültige Website-URL")
    private String website;
    @Pattern(regexp = "^DE\\d{10,11}$", message = "Ungültige Steuer-Identifikationsnummer. Muss mit 'DE' beginnen und 10 oder 11 Ziffern enthalten.")
    private String taxNumber;
    private String logo;
    @Pattern(regexp = "https?://.+", message = "Ungültige URL für die Datenschutzerklärung")
    private String privacyPolicyUrl;
    private String extras;

    @Field("preferences")
    @Valid
    @NotNull(message = "Einstellungen sind Pflichtfelder.")
    private ClientPreferences preferences;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public String getFullName() {
        return firstName + " " + lastName;
    }
    public byte[] getLogoAsByteArray() {
        // Check if the string contains the data URI prefix
        String base64Data;
        if (logo.startsWith("data:image")) {
            // Remove the prefix
            base64Data = logo.split(",")[1];
        } else {
            base64Data = logo;
        }

        // Decode the Base64 string to a byte array
        return Base64.getDecoder().decode(base64Data);
    }
}
