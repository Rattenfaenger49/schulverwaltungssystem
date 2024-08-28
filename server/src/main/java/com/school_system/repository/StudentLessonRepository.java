package com.school_system.repository;

import com.school_system.entity.school.StudentLesson;
import com.school_system.entity.school.StudentLessonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentLessonRepository extends JpaRepository<StudentLesson, StudentLessonId> {
}
