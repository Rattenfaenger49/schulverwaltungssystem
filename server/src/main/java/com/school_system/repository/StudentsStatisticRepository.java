package com.school_system.repository;

import com.school_system.entity.school.Views.StudentsStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsStatisticRepository extends JpaRepository<StudentsStatistic, Long> {
}
