package com.school_system.repository;

import com.school_system.entity.school.Views.AdminStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminStatisticRepository extends JpaRepository<AdminStatistic, Long> {
    @Query("SELECT a FROM AdminStatistic a")
    AdminStatistic getAdminStatistic();
}
