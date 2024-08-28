package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.entity.school.Views.AdminStatistic;
import com.school_system.entity.school.Views.StudentsStatistic;
import com.school_system.entity.school.Views.TeachersStatistic;
import com.school_system.service.StatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/statisitcs")
public class StatisticsController {
    private final StatisticsService statisticsService;
    @GetMapping("/admin")
    ResponseObject<AdminStatistic>adminStatistic(){
        return statisticsService.adminStatistic();

    }
    @GetMapping("/teacher")
    ResponseObject<TeachersStatistic>teacherStatistics(){
        return statisticsService.teachersStatistic();

    }
    @GetMapping("/student")
    ResponseObject<StudentsStatistic>studentStatistics(){
        return statisticsService.studentsStatistic();

    }
}
