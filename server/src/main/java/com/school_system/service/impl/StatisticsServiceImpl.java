package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.entity.school.Views.AdminStatistic;
import com.school_system.entity.school.Views.StudentsStatistic;
import com.school_system.entity.school.Views.TeachersStatistic;
import com.school_system.repository.*;
import com.school_system.service.StatisticsService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final AdminStatisticRepository adminStatisticRepository;
    private final TeachersStatisticRepository teachersStatisticRepository;
    private final StudentsStatisticRepository studentsStatisticRepository;

    @Override
    public ResponseObject<AdminStatistic> adminStatistic() {
        try {

            AdminStatistic AdminStatistic = adminStatisticRepository.getAdminStatistic();


            return ResponseObject.<AdminStatistic>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(AdminStatistic)
                    .build();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen von Statistiken.", e);
            throw new RuntimeException("Fehler beim Abrufen von Statistiken.");
        }
    }

    @Override
    public ResponseObject<TeachersStatistic> teachersStatistic() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long id = UserUtils.getUserId(authentication);

            TeachersStatistic teacherStatistic = teachersStatisticRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Etwas ist schiefgelaufen, Statistiken nicht gefunden!")
            );

            return ResponseObject.<TeachersStatistic>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(teacherStatistic)
                    .build();

    }

    @Override
    public ResponseObject<StudentsStatistic> studentsStatistic() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long id = UserUtils.getUserId(authentication);

            StudentsStatistic studentStatistics = studentsStatisticRepository.findById(id).orElseThrow(()
            -> new EntityNotFoundException("Etwas ist schiefgelaufen"));

            return ResponseObject.<StudentsStatistic>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(studentStatistics)
                    .build();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen von Statistiken.", e);
            throw new RuntimeException("Fehler beim Abrufen von Statistiken.");
        }
    }


}
