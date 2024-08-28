package com.school_system.repository;

import com.school_system.entity.school.Invoice;

import com.school_system.entity.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Custom query to find the last generated invoice for a specific teacher
    Optional<Invoice> findFirstByUserIdOrderByInvoiceDateDescIdDesc(Long teacherId);

    Page<Invoice> findAllByUserId(Long teacherId, Pageable pageable);

    Optional<Invoice> findByPeriodStartDateAndUser(LocalDate startOfMonth, User user);

    @Query("SELECT MAX(CAST(SUBSTRING(i.invoiceNumber, -3, 3) AS int)) " +
            "FROM Invoice i " +
            "WHERE FUNCTION('TO_CHAR', i.periodStartDate, 'YYMM') = :yearMonth " +
            "AND i.user.id = :userId")
    Integer findMaxInvoiceNumber(@Param("yearMonth") String yearMonth, @Param("userId") Long userId);
}
