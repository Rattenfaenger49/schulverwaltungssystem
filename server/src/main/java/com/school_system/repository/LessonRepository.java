package com.school_system.repository;
// TODO optimize using specificatio c
import com.school_system.entity.school.Lesson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT DISTINCT l FROM Lesson l JOIN FETCH l.studentLessons s WHERE s.id = :studentId")
    Page<Lesson> findByStudentId(Long studentId, Pageable pageable);

    @Query("SELECT DISTINCT l FROM Lesson l JOIN FETCH l.teacher t WHERE" +
            " l.id = :id AND t.id = :teacherId ")
    Optional<Lesson> findLessonByTeacherId(Long id,Long teacherId);

    @Query("SELECT DISTINCT l FROM Lesson l JOIN FETCH l.studentLessons s " +
            "WHERE l.id = :id AND  :studentId IN (SELECT student.id FROM l.studentLessons student)")
    Optional<Lesson> findLessonByStudentId(Long id, Long studentId);

    // without left join to l.teachers the query will not return lessons without teachers
    @Query(" SELECT DISTINCT l  FROM Lesson l " +
            "LEFT JOIN FETCH l.teacher t " +
            "LEFT JOIN FETCH l.studentLessons s " +
            "WHERE   ( :query = '' OR " +
            "( (l.teacher IS NULL OR LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            " OR  LOWER(l.modulType) LIKE LOWER(CONCAT('%', :query, '%'))" +
            " OR  LOWER(CONCAT(s.student.firstName, ' ', s.student.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))))")
    Page<Lesson> searchLessons(@Param("query")  String query, Pageable pageable);

    @Query("SELECT DISTINCT l FROM Lesson l " +
            "LEFT JOIN FETCH l.teacher t " +
            "LEFT JOIN FETCH l.studentLessons s " +
            "WHERE t.id = :teacherId " +
            "AND ((:studentId IS NULL) OR (s.student.id = :studentId)) " +
            "AND (:query = '' " +
            "     OR LOWER(l.modulType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(CONCAT(s.student.firstName, ' ', s.student.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Lesson> searchLessonsByTeacherId(@Param("query") String query,
                                          @Param("teacherId") long teacherId,
                                          @Param("studentId") long studentId,
                                          Pageable pageable);

    // without left join to l.teachers the query will not return lessons without teachers
    @Query("SELECT DISTINCT l FROM Lesson l " +
            "LEFT JOIN l.studentLessons s " +
            "LEFT JOIN l.teacher t " +
            "WHERE s.student.id = :studentId " +
            "AND (:query = '' " +
            "     OR t IS NULL " +
            "     OR LOWER(l.modulType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Lesson> searchLessonsByStudentId(@Param("query") String query, @Param("studentId") long studentId, Pageable pageable);


    // FOR Documentation
    @Query("SELECT l FROM Lesson l JOIN StudentLesson sl ON l.id = sl.lesson.id " +
            " WHERE sl.student.id = :studentId AND l.startAt BETWEEN :startDate AND :endDate " +
            " ORDER BY l.startAt ASC")
    List<Lesson> findByStudentsIdAndStartAtBetweenOrderByStartAtAsc(Long studentId, LocalDateTime startDate, LocalDateTime endDate);



    List<Lesson> findByTeacherIdAndStartAtBetweenOrderByStartAtAsc(Long teacherId, LocalDateTime localDateTime, LocalDateTime localDateTime1);

}
