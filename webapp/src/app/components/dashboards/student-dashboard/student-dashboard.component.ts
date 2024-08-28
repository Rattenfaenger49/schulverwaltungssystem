import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {DProgressBar} from "../../../_services/directivs/DProgressBar";
import {DecimalPipe} from "@angular/common";
import {StudentsStatistic} from "../../../types/app-statistics";
import {FormBuilder, FormGroup} from "@angular/forms";
import {StatisitcsService} from "../../../_services/data/statistics.service";

@Component({
	selector: "ys-student-dashboard",
	standalone: true,
	imports: [DProgressBar, DecimalPipe],
	templateUrl: "./student-dashboard.component.html",
	styleUrl: "./student-dashboard.component.scss",
})
export class StudentDashboardComponent implements OnInit {
	studentStatistics: WritableSignal<StudentsStatistic | null> = signal(null);
	form: FormGroup = this.fb.group({
		startDate: [""],
		endDate: [""],
	});
	
	constructor(
		private statistcsService: StatisitcsService,
		private fb: FormBuilder,
	) {
	}
	
	ngOnInit(): void {
		this.statistcsService.studentStatistics()
			.subscribe({
				next: (res) => {
					this.studentStatistics.set(res.data);
					
				}
			});
	}
	
	pWeeklyUnitsInWeek() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsWeek !== 0
			? this.studentStatistics()!.takenUnitsForWeeklyContractsInWeek /
			this.studentStatistics()!.targetUnitsWeek
			: 0;
	}
	
	pWeeklyUnitsInMonth() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsWeek !== 0
			? this.studentStatistics()!.takenUnitsForWeeklyContractsInMonth /
			(this.studentStatistics()!.targetUnitsWeek * 4)
			: 0;
	}
	
	pMonthlyUnitsInWeek() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsMonth !== 0
			? this.studentStatistics()!.takenUnitsForMonthlyContractsInWeek /
			(this.studentStatistics()!.targetUnitsMonth / 4)
			: 0;
	}
	
	pMonthlyUnitsInMonth() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsMonth !== 0
			? this.studentStatistics()!.takenUnitsForMonthlyContractsInMonth /
			(this.studentStatistics()!.targetUnitsMonth)
			: 0;
	}
	
	pPeriodUnitsInWeek() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsPeriod !== 0
			? this.studentStatistics()!.takenUnitsForPeriodContractsInWeek /
			this.studentStatistics()!.targetUnitsPeriod
			: 0;
	}
	
	pPeriodUnitsInMonth() {
		if (!this.studentStatistics) return 0.0;
		return this.studentStatistics()!.targetUnitsPeriod !== 0
			? this.studentStatistics()!.takenUnitsForPeriodContractsInMonth /
			this.studentStatistics()!.targetUnitsPeriod
			: 0;
	}
	
}

