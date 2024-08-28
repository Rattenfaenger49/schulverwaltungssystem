package com.school_system.repository;

import com.school_system.entity.school.Institution;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    Optional<Institution> findByName(String name);

    @Query("SELECT i FROM Institution i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR i.phoneNumber LIKE CONCAT('%', :query, '%') ")
    Page<Institution> searchInstitutions( String query, Pageable pageable);
}
