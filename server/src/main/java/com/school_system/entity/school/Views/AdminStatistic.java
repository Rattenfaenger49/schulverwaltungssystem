package com.school_system.entity.school.Views;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;


// This class represents the AdminStatistic View in the database
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Table(name = "admin_statistic")
public class AdminStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Dummy primary key
    // Teacher Statistcs
    private Integer teachersNumber;
    private Integer teachersWithoutLessons;
    private Integer teachersWithoutStudents;
    private Integer markedForDeletionTeachers;
    // Students Statiscs
    private Integer studentsNumber;
    private Integer studentsWithoutLessons;
    private Integer studentsWithoutTeachers;
    private Integer markedForDeletionStudents;
    // Lessons Statistcs
    private Integer givenLessons;
    private Double givenUnits;
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


    private Integer givenLessonsInWeek;
    private Integer givenLessonsInMonth;
    // Contracts Statistics
    private Integer contractsNumber;
    private Double targetUnitsWeek;
    private Double targetUnitsMonth;
    private Double targetUnitsPeriod;
    private Integer activeContracts;
    private Integer inactiveContracts;
    private Integer terminatedContracts;
    private Integer blockedContracts;
    private Integer inProgressContracts;


}
