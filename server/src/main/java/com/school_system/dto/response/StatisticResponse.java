package com.school_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {
    private long teacherTotal;
    private long studentTotal;
    private long contractTotal;
    private long lessonsTotal;
    private long lessonsTotalInWeek;
    private long lessonsTotalInMonth;
    private long studentCountWithNoTeacher;
    private long teacherWithNoStudent;
    private double unitsTotalInWeek;
    private double unitsTotalInMonth;
    private double givenUnitsInWeek;
    private double givenUnitsInMonth;

}
