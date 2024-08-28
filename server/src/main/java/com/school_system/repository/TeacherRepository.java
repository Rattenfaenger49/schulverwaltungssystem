package com.school_system.repository;

import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.entity.security.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {


    @Query("SELECT t FROM Teacher t left join fetch t.students s " +
            " WHERE (:filter IS NULL OR :filter = ''  OR " +
            " (:filter = 'WITH_STUDENTS' AND s IS NOT NULL ) OR " +
            " (:filter = 'DELETION' AND s.markedForDeletion = true ) OR " +
            " (:filter = 'WITHOUT_STUDENTS' AND s IS NULL )) AND" +
            " (LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) " +
            " OR LOWER(t.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
            " OR LOWER(t.username) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Teacher> searchTeachers(String filter,String query, Pageable pageable);

    @Query("SELECT new com.school_system.dto.response.UserFullNameResponse(t.id, t.firstName, t.lastName)" +
            "FROM Teacher t WHERE t.markedForDeletion = false")
    List<UserFullNameResponse>findAllWithFullname();

    @Query("SELECT new com.school_system.dto.response.UserFullNameResponse(s.id, s.firstName, s.lastName)" +
            "FROM Teacher t JOIN  t.students s  WHERE t.id = :id AND t.markedForDeletion = false")
    List<UserFullNameResponse>findStudentsWithFullname(@Param("id") Long id);

}
