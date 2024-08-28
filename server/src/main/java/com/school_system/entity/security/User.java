package com.school_system.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.school.Address;
import com.school_system.enums.school.Gender;
import com.school_system.util.RolesSetDeserializer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@SuperBuilder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String firstName;

    private String lastName;

    private LocalDate birthdate;

    private String phoneNumber;

    private String comment;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String username;
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = false;
    @Builder.Default
    private boolean verified = false;
    @Builder.Default
    private boolean hasProvidedAllInfo = true;
    @Builder.Default
    private boolean markedForDeletion = false;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonDeserialize(using = RolesSetDeserializer.class)
    private Set<Role> roles;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;


    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return accountNonExpired == user.accountNonExpired
                && accountNonLocked == user.accountNonLocked &&
                credentialsNonExpired == user.credentialsNonExpired &&
                enabled == user.enabled && verified == user.verified &&
                hasProvidedAllInfo == user.hasProvidedAllInfo &&
                Objects.equals(id, user.id) && gender == user.gender &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(birthdate, user.birthdate) &&
                Objects.equals(password, user.password) &&
                Objects.equals(username, user.username) &&
                Objects.equals(comment, user.comment) &&
                Objects.equals(roles, user.roles) &&
                Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gender, firstName, lastName,birthdate, phoneNumber, password, username,comment, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled, verified, hasProvidedAllInfo, roles, address);
    }

    public void deleteProfile() {
        this.setAccountNonLocked(false);
        this.markedForDeletion = true;


    }

    public void undoDeleteProfile() {
        this.setAccountNonLocked(true);
        this.markedForDeletion = false;
    }
}
