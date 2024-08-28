package com.school_system.repository;

import com.school_system.entity.school.Contract;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByContractNumber(String contractNumber);

    @Query("SELECT c FROM Contract c JOIN fetch c.student s WHERE s.id = :studentId")
    List<Contract> findByStudentId(Long studentId);

    @Query("SELECT c FROM Contract c JOIN fetch c.student s WHERE " +
            " (c.status = :contractStatus OR :contractStatus = 'ANY') AND " +
            " (c.contractType = :contractType OR :contractType = 'ANY') AND " +
            " ( LOWER(c.contractNumber) LIKE LOWER(concat('%', :query, '%')) " +
            " OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<Contract> findAllWithQuery(String query, ContractType contractType, ContractStatus contractStatus, Pageable pageable);

    @Query("SELECT c FROM Contract c JOIN fetch c.student s WHERE" +
            " c.student.id = :studentId AND " +
            " (c.status = :contractStatus OR :contractStatus = 'ANY') AND " +
            " (c.contractType = :contractType OR :contractType = 'ANY') AND " +
            " (" +
            "LOWER(c.contractNumber) LIKE LOWER(concat('%', :query, '%'))" +
            " OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<Contract> findAllWithQueryAndStudent(@Param("query") String query,
                                              ContractType contractType,
                                              ContractStatus contractStatus,
                                              @Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT c FROM Contract c " +
            "WHERE c.student.id = :studentId " +
            "AND c.contractType = :contractType " +
            "AND c.startAt <= :endAt " +
            "AND c.endAt >= :startAt")
    Optional<Contract> findContractsByStudentIdAndOverlap(
            @Param("studentId") Long studentId,
            @Param("contractType") ContractType contractType,
            @Param("startAt") LocalDate startAt,
            @Param("endAt") LocalDate endAt);
}

