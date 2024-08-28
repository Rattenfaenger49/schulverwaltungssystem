package com.school_system.repository;

import com.school_system.entity.school.BankData;
import com.school_system.entity.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDataRepository extends JpaRepository<BankData, Long> {

    Optional<BankData> findByUser(User user);
}
