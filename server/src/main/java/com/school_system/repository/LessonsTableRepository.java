package com.school_system.repository;

import com.school_system.entity.school.Views.LessonsTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LessonsTableRepository extends JpaRepository<LessonsTable, Long> {
    // Check Usage
    @Query("SELECT l FROM LessonsTable l WHERE l.teacherId = ?1")
    Page<LessonsTable> findLessonsByTeacherId(Long id, Pageable pageable);

}
