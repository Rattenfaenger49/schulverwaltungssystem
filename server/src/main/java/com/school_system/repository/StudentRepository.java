package com.school_system.repository;

import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("SELECT s FROM Student s " +
            " WHERE (((:filter IS NULL OR :filter = '' )OR " +
            "(:filter = 'WITHOUT_TEACHER' AND s.teachers IS EMPTY ) OR " +
            "      (:filter = 'WITH_TEACHER' AND s.teachers IS NOT EMPTY ) OR " +
            "      (:filter = 'WITHOUT_CONTRACT' AND s.contracts IS EMPTY ) OR " +
            "      (:filter = 'WITHOUT_LESSON' AND s.studentLessons IS EMPTY ) OR " +
            "      (:filter = 'WITH_LESSON' AND s.studentLessons IS NOT EMPTY) OR " +
            "      (:filter = 'DELETION' AND s.markedForDeletion = true ) OR " +
            "      (:filter = 'WITH_CONTRACT' AND s.contracts IS NOT EMPTY)) AND (" +
            " LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))  OR" +
            " LOWER(s.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))))")
    Page<Student> searchStudentsFilter(String query,String filter, Pageable pageable);

    @Query("SELECT s FROM Student s " +
            " WHERE :teacher MEMBER OF s.teachers AND ((:filter IS NULL OR :filter = '' ) OR " +
            "(:filter = 'WITHOUT_TEACHER' AND s.teachers IS EMPTY ) OR " +
            "      (:filter = 'WITH_TEACHER' AND s.teachers IS NOT EMPTY ) OR " +
            "      (:filter = 'WITHOUT_CONTRACT' AND s.contracts IS EMPTY ) OR " +
            "      (:filter = 'WITHOUT_LESSON' AND s.studentLessons IS EMPTY ) OR " +
            "      (:filter = 'WITH_LESSON' AND s.studentLessons IS NOT EMPTY ) OR " +
            "      (:filter = 'DELETION' AND s.markedForDeletion = true ) OR " +
            "      (:filter = 'WITH_CONTRACT' AND s.contracts IS  NOT EMPTY )) AND " +
            " ( LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(s.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Student> searchAssociatedStudentsByTeacherId(String query, String filter, @Param("teacher") Teacher teacher , Pageable pageable);

    @Query("SELECT new com.school_system.dto.response.UserFullNameResponse(s.id, s.firstName, s.lastName) FROM Student s" +
            " WHERE s.markedForDeletion = false")
    List<UserFullNameResponse> findAllWithFullname();

    @Query("SELECT s FROM Student s JOIN FETCH s.teachers t WHERE t.username = :teacherEmail " +
            "AND  s.markedForDeletion = false")
    Page<Student> findAssociatedStudentsByTeacherId(@Param("teacherEmail") String teacherEmail, Pageable pageable);


    Optional<Student> findByIdAndMarkedForDeletionFalse(Long id);
}
