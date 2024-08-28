package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.entity.school.Views.AdminStatistic;
import com.school_system.entity.school.Views.StudentsStatistic;
import com.school_system.entity.school.Views.TeachersStatistic;

public interface StatisticsService {
    ResponseObject<AdminStatistic> adminStatistic();

    ResponseObject<TeachersStatistic> teachersStatistic();

    ResponseObject<StudentsStatistic> studentsStatistic();
}
