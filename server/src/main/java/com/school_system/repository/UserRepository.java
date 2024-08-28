package com.school_system.repository;

import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.entity.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u JOIN FETCH u.address   WHERE u.username = :username")
    Optional<User> findByUsername(String username);
    @Query("SELECT COUNT(u)>0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(String username);

    @Query("SELECT new com.school_system.dto.response.UserFullNameResponse(u.id, u.firstName, u.lastName) " +
            "FROM User u RIGHT JOIN FETCH Student s  ON u.id= s.id  WHERE u.markedForDeletion = false")
    List<UserFullNameResponse> findStudentsSelectList();


    @Query("SELECT u FROM User u WHERE u.markedForDeletion = true AND u.updatedAt < :timestamp")
    List<User> findByMarkedForDeletionTrueAndupdatedAtBefore(LocalDateTime timestamp);
    @Query("SELECT u FROM User u WHERE u.markedForDeletion = true")
    List<User> findByMarkedForDeletionTrue();
}
