
export type AdminStatistic = {
    teachersNumber: number;
    teachersWithoutLessons: number;
    teachersWithoutStudents: number;
    markedForDeletionTeachers: number;
    studentsNumber: number;
    studentsWithoutLessons: number;
    studentsWithoutTeachers: number;
    markedForDeletionStudents: number;
    givenLessons: number;
    givenUnits: number;
    givenLessonsForWeeklyContracts: number;
    givenLessonsForMonthlyContracts: number;
    givenLessonsForPeriodContracts: number;
    givenLessonsForIndividualContracts: number;
    givenUnitsForWeeklyContractsInWeek: number;
    givenUnitsForMonthlyContractsInWeek: number;
    givenUnitsForPeriodContractsInWeek: number;
    givenUnitsForIndividualContractsInWeek: number;
    givenUnitsForWeeklyContractsInMonth: number;
    givenUnitsForMonthlyContractsInMonth: number;
    givenUnitsForPeriodContractsInMonth: number;
    givenUnitsForIndividualContractsInMonth: number;
    givenLessonsInWeek: number;
    givenLessonsInMonth: number;
    contractsNumber: number;
    targetUnitsWeek: number;
    targetUnitsMonth: number;
    targetUnitsPeriod: number;
    activeContracts: number;
    inactiveContracts: number;
    terminatedContracts: number;
    blockedContracts: number;
    inProgressContracts: number;

}

export type StudentsStatistic = {
    targetUnits: number;
    targetUnitsWeek: number;
    targetUnitsMonth: number;
    targetUnitsPeriod: number;
    takenUnitsWeek: number;
    takenUnitsMonth: number;
    takenUnitsPeriod: number;
    takenUnitsForWeeklyContractsInWeek: number;
    takenUnitsForMonthlyContractsInWeek: number;
    takenUnitsForPeriodContractsInWeek: number;
    takenUnitsForIndividualContractsInWeek: number;
    takenUnitsForWeeklyContractsInMonth: number;
    takenUnitsForMonthlyContractsInMonth: number;
    takenUnitsForPeriodContractsInMonth: number;
    takenUnitsForIndividualContractsInMonth: number;
    takenLessonsInWeek: number;
    takenLessonsInMonth: number;
    takenLessons: number;
    takenUnitsForWeeklyContracts: number;
    takenUnitsForMonthlyContracts: number;
    takenUnitsForPeriodContracts: number;
    takenUnitsForIndividualContracts: number;
};
export type TeachersStatistic = {
    targetUnits: number;
    targetUnitsWeek: number;
    targetUnitsMonth: number;
    targetUnitsPeriod: number;
    givenUnitsForWeeklyContractsInWeek: number;
    givenUnitsForMonthlyContractsInWeek: number;
    givenUnitsForPeriodContractsInWeek: number;
    givenUnitsForIndividualContractsInWeek: number;
    givenUnitsForWeeklyContractsInMonth: number;
    givenUnitsForMonthlyContractsInMonth: number;
    givenUnitsForPeriodContractsInMonth: number;
    givenUnitsForIndividualContractsInMonth: number;
    givenPrivateUnitsInWeek: number;
    givenLessonsInWeek: number;
    givenLessons: number;
    givenLessonsInMonth: number;
    givenUnitsForWeeklyContracts: number;
    givenUnitsForMonthlyContracts: number;
    givenUnitsForPeriodContracts: number;
}

