package com.school_system.entity.school.Views;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;


// This class represents the AdminStatistic View in the database
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Table(name = "admin_lessons_Statistic")
public class AdminLessonsStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Dummy primary key
    // Lessons and givenunits
    private Integer givenLessons;
    private Integer givenLessonsForWeeklyContracts;
    private Integer givenLessonsForMonthlyContracts;
    private Integer givenLessonsForPeriodContracts;
    private Integer givenLessonsForIndividualContracts;

    private Double givenUnitsForWeeklyContractsInWeek;
    private Double givenUnitsForMonthlyContractsInWeek;
    private Double givenUnitsForPeriodContractsInWeek;
    private Double givenUnitsForIndividualContractsInWeek;

    private Double givenUnitsForWeeklyContractsInMonth;
    private Double givenUnitsForMonthlyContractsInMonth;
    private Double givenUnitsForPeriodContractsInMonth;
    private Double givenUnitsForIndividualContractsInMonth;

    private Double givenNormalUnitsInMonth;
    private Double givenHolidayUnitsInMonth;
    private Double givenPrivateUnitsInMonth;

    private Integer givenLessonsInWeek;
    private Integer givenLessonsInMonth;


}
