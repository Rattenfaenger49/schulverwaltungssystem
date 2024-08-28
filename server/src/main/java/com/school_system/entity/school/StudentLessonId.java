package com.school_system.entity.school;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@RequiredArgsConstructor
public class StudentLessonId implements Serializable {
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "lesson_id")
    private Long lessonId;

    // equals and hashCode methods
}