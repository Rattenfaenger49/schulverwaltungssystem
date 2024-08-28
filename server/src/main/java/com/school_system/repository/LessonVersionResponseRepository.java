package com.school_system.repository;

import com.school_system.entity.school.Views.LessonVersionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonVersionResponseRepository extends JpaRepository<LessonVersionResponse, Long> {

    @Query("SELECT lvr FROM LessonVersionResponse lvr WHERE lvr.lessonId = :lessonId")
    List<LessonVersionResponse> findByLessonIdOrderByCreatedAtDesc(long lessonId);
}
