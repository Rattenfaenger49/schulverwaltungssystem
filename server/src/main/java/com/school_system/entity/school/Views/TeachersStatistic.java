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
@Table(name = "teachers_statistic")
public class TeachersStatistic {

    @Id
    private Long id;
    private Double targetUnits;
    private Double targetUnitsWeek;
    private Double targetUnitsMonth;
    private Double targetUnitsPeriod;
    private Double targetUnitsIndividually;
    private Double givenUnitsForWeeklyContractsInWeek;
    private Double givenUnitsForMonthlyContractsInWeek;
    private Double givenUnitsForPeriodContractsInWeek;
    private Double givenUnitsForIndividualContractsInWeek;
    private Double givenUnitsForWeeklyContractsInMonth;
    private Double givenUnitsForMonthlyContractsInMonth;
    private Double givenUnitsForPeriodContractsInMonth;
    private Double givenUnitsForIndividualContractsInMonth;
    private Double givenUnitsForWeeklyContracts;
    private Double givenUnitsForMonthlyContracts;
    private Double givenUnitsForPeriodContracts;
    private Integer givenLessonsInWeek;
    private Integer givenLessons;
    private Integer givenLessonsInMonth;

}
