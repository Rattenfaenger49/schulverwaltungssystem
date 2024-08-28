package com.school_system.entity.school;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.dto.request.ContactRequest;

import com.school_system.enums.school.Gender;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String updatedBy;
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id) && gender == contact.gender && Objects.equals(firstName, contact.firstName) && Objects.equals(lastName, contact.lastName) && Objects.equals(phoneNumber, contact.phoneNumber) && Objects.equals(email, contact.email);
    }

    public boolean equals(ContactRequest o) {

        return Objects.equals(id, o.getId()) && gender == o.getGender() && Objects.equals(firstName, o.getFirstName()) && Objects.equals(lastName, o.getLastName()) && Objects.equals(phoneNumber, o.getPhoneNumber()) && Objects.equals(email, o.getEmail());
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, gender, firstName, lastName, phoneNumber, email, institution);
    }
}
