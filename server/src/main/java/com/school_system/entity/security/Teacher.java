package com.school_system.entity.security;


import com.school_system.entity.school.Lesson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends User {

    @Column
    private String qualifications;

    @Column
    private String education;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal singleLessonCost = BigDecimal.valueOf(0.0);

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal groupLessonCost = BigDecimal.valueOf(0.0);

    @Column(nullable = false)
    private String taxId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teachers_students",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teacher")
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();
    public void addStudent(Student student) {
        students.add(student);
        student.getTeachers().add(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.getTeachers().remove(this);
    }
    @Override
    public void deleteProfile() {
        super.deleteProfile();
    }


    @Override
    public String toString() {
        return "Teacher{" +
                // Add teacher-specific attributes
                "qualifications='" + qualifications + '\'' +
                ", education='" + education + '\'' +
                // Call toString of User to include user details
                ", user=" + super.toString() +
                '}';
    }
}
