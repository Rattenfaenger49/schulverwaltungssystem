import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {TeachersStatistic} from "../../../types/app-statistics";
import {FormBuilder, FormGroup} from "@angular/forms";
import {StatisitcsService} from "../../../_services/data/statistics.service";
import {DProgressBar} from "../../../_services/directivs/DProgressBar";
import {DecimalPipe} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@Component({
	selector: "ys-teacher-dashboard",
	standalone: true,
	imports: [DProgressBar, DecimalPipe, MatIconModule],
	templateUrl: "./teacher-dashboard.component.html",
	styleUrl: "./teacher-dashboard.component.scss",
})
export class TeacherDashboardComponent implements OnInit {
	teacherStatistics: WritableSignal<TeachersStatistic | null> = signal(null);
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
		this.statistcsService.teacherStatistic()
			.subscribe({
				next: (res) => {
					this.teacherStatistics.set(res.data);
					
				}
			});
	}
	
	pWeeklyUnitsInWeek(): number {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsWeek !== 0
			? this.teacherStatistics()!.givenUnitsForWeeklyContractsInWeek /
			this.teacherStatistics()!.targetUnitsWeek
			: 0;
	}
	
	pWeeklyUnitsInMonth(): number {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsWeek !== 0
			? this.teacherStatistics()!.givenUnitsForWeeklyContractsInMonth /
			(this.teacherStatistics()!.targetUnitsWeek * 4)
			: 0;
	}
	
	pMonthlyUnitsInWeek(): number {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsMonth !== 0
			? this.teacherStatistics()!.givenUnitsForMonthlyContractsInWeek /
			this.teacherStatistics()!.targetUnitsMonth
			: 0;
	}
	
	pMonthlyUnitsInMonth(): number {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsMonth !== 0
			? this.teacherStatistics()!.givenUnitsForMonthlyContractsInMonth /
			this.teacherStatistics()!.targetUnitsMonth
			: 0;
	}
	
	
	pPeriodUnitsInWeek() {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsPeriod !== 0
			? this.teacherStatistics()!.givenUnitsForPeriodContractsInMonth /
			this.teacherStatistics()!.targetUnitsPeriod
			: 0;
	}
	
	pPeriodUnitsInMonth() {
		if (!this.teacherStatistics) return 0.0;
		return this.teacherStatistics()!.targetUnitsPeriod !== 0
			? this.teacherStatistics()!.givenUnitsForPeriodContractsInMonth /
			this.teacherStatistics()!.targetUnitsPeriod
			: 0;
	}
}
