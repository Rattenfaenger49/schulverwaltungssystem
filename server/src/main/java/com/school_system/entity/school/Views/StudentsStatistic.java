package com.school_system.entity.school.Views;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "students_statistic")
public class StudentsStatistic {

    @Id
    private Long id;
    private Double targetUnits;
    private Double targetUnitsWeek;
    private Double targetUnitsMonth;
    private Double targetUnitsPeriod;
    private Double takenUnitsWeek;
    private Double takenUnitsMonth;
    private Double takenUnitsPeriod;
    private Double takenUnitsForWeeklyContractsInWeek;
    private Double takenUnitsForMonthlyContractsInWeek;
    private Double takenUnitsForPeriodContractsInWeek;
    private Double takenUnitsForIndividualContractsInWeek;
    private Double takenUnitsForWeeklyContractsInMonth;
    private Double takenUnitsForMonthlyContractsInMonth;
    private Double takenUnitsForPeriodContractsInMonth;
    private Double takenUnitsForIndividualContractsInMonth;
    private Integer takenLessonsInWeek;
    private Integer takenLessonsInMonth;
    private Integer takenLessons;
    private Double takenUnitsForWeeklyContracts;
    private Double takenUnitsForMonthlyContracts;
    private Double takenUnitsForPeriodContracts;
    private Double takenUnitsForIndividualContracts;

}
