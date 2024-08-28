package com.school_system.repository;

import com.school_system.entity.school.LessonVersion;
import com.school_system.entity.school.Views.LessonVersionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonVersionRepository extends JpaRepository<LessonVersion, Long> {


    /**
     * Find a specific version of a lesson by its ID and version number.
     *
     * @param lessonId the ID of the lesson.
     * @param createdDate the version number of the lesson.
     * @return an Optional containing the LessonVersion if found.
     */
    Optional<LessonVersion> findByLessonIdAndCreatedAt(long lessonId, LocalDateTime createdDate);

    /**
     * Find the latest version of a lesson.
     *
     * @param lessonId the ID of the lesson.
     * @return an Optional containing the latest LessonVersion if found.
     */
    Optional<LessonVersion> findTopByLessonIdOrderByCreatedAtDesc(long lessonId);
}
