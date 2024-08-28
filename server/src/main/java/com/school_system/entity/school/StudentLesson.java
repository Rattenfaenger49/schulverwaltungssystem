package com.school_system.entity.school;


import com.school_system.entity.security.Student;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "students_lessons")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentLesson implements Serializable {

    @EmbeddedId
    @Builder.Default
    private StudentLessonId id = new StudentLessonId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modul_id", nullable = false)
    private Modul modul;

}

