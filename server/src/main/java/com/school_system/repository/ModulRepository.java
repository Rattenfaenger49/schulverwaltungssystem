package com.school_system.repository;

import com.school_system.entity.school.Modul;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ModulRepository extends JpaRepository<Modul, Long> {
    @Query("SELECT COALESCE(SUM(m.units), 0) FROM Modul m")
    double sumUnits();
}
