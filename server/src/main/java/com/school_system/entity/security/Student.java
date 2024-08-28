package com.school_system.entity.security;


import com.school_system.entity.school.Contract;
import com.school_system.entity.school.Lesson;
import com.school_system.entity.school.Parent;
import com.school_system.entity.school.StudentLesson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student extends User {


    private String level;
    private boolean portalAccess;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentLesson> studentLessons = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @Builder.Default
    private List<Contract> contracts = new ArrayList<>();


    @ManyToMany(mappedBy = "students")
    @Builder.Default
    private List<Teacher> teachers = new ArrayList<>();

    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Override
    public void deleteProfile() {
        super.deleteProfile();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return portalAccess == student.portalAccess &&
                Objects.equals(level, student.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), level, portalAccess);
    }

    public String toString() {
        return "Student( "+   super.toString() +", level=" + this.getLevel() + ", portalAccess=" + this.isPortalAccess() + ", parent=" + this.getParent() + ")";
    }
}
