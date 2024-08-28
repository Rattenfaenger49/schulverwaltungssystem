package com.school_system.entity.security;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;



@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supervisors")
public class Supervisor extends User {
    private String section;


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
