package com.school_system.entity.school;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.LessonType;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "lessons")
@EntityListeners(AuditingEntityListener.class)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModulType modulType;

    @Column(nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private Double units;


    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false) // Adjust the length as needed
    private LessonType lessonType;
    @Column(length = 512, nullable = false)
    @NonNull
    private String description;

    @Column(length = 512)
    private String comment;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @OneToMany(mappedBy = "lesson", orphanRemoval = true)
    @Builder.Default
    private List<StudentLesson> studentLessons = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lessons_signatures",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "signature_id"))
    @Builder.Default
    private List<Signature> signature = new ArrayList<>();
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Method to add a student to the lesson
    public void addStudent(Student student, Modul modul) {
        StudentLesson studentLesson = new StudentLesson();
        studentLesson.setLesson(this);
        studentLesson.setStudent(student);
        studentLesson.setModul(modul);

        this.studentLessons.add(studentLesson);
        student.getStudentLessons().add(studentLesson);
    }
/*
    // Method to remove a student from the lesson
    public void removeStudentLesson(StudentLesson studentLesson) {
        this.studentLessons.remove(studentLesson);
        studentLesson.getStudent().getStudentLessons().remove(studentLesson);
        studentLesson.setLesson(null);
        studentLesson.setStudent(null);
    }*/

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Lesson lesson = (Lesson) o;
        return getId() != null && Objects.equals(getId(), lesson.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void addSignautre(Signature signature) {
        this.signature.add(signature);
    }
}
