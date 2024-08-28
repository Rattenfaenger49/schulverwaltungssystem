import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {DProgressBar} from "../../../_services/directivs/DProgressBar";
import {DecimalPipe} from "@angular/common";
import {FormBuilder, FormGroup} from "@angular/forms";
import {StatisitcsService} from "../../../_services/data/statistics.service";
import {RouterLink} from "@angular/router";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {AdminStatistic} from "../../../types/app-statistics";
import {NgxExtendedPdfViewerModule} from "ngx-extended-pdf-viewer";


@Component({
  selector: "ys-admin-dashboard",
  standalone: true,
	imports: [DProgressBar, DecimalPipe, RouterLink, LoaderComponent, NgxExtendedPdfViewerModule],
  templateUrl: "./admin-dashboard.component.html",
  styleUrl: "./admin-dashboard.component.scss",
})
export class AdminDashboardComponent implements OnInit {
  appStatistics: WritableSignal<AdminStatistic | null> = signal(null);
  form: FormGroup = this.fb.group({
    startDate: [""],
    endDate: [""],
  });
  constructor(
      private statistcsService: StatisitcsService,
      private fb: FormBuilder,
) {}

  ngOnInit(): void {
    
    this.statistcsService.adminStatistic().subscribe({
      next: (res) => {
        this.appStatistics.set(res.data);

      }
    });
  }
  
  givenWeeklyUnitsInWeekPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsWeek !== 0
        ? this.appStatistics()!.givenUnitsForWeeklyContractsInWeek /
        this.appStatistics()!.targetUnitsWeek
        : 0;
  }
  givenWeeklyUnitsInMonthPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsWeek !== 0
        ? this.appStatistics()!.givenUnitsForWeeklyContractsInMonth /
        (this.appStatistics()!.targetUnitsWeek * 4)
        : 0;
  }

  givenMonthlyUnitsInWeekPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsMonth !== 0
        ? this.appStatistics()!.givenUnitsForMonthlyContractsInWeek /
        this.appStatistics()!.targetUnitsMonth / 4
        : 0;
  }
  givenMonthlyUnitsInMonthPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsMonth !== 0
        ? this.appStatistics()!.givenUnitsForMonthlyContractsInMonth /
        this.appStatistics()!.targetUnitsMonth
        : 0;
  }
  
  
  givenPeriodUnitsInWeekPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsPeriod !== 0
        ? this.appStatistics()!.givenUnitsForPeriodContractsInWeek /
        this.appStatistics()!.targetUnitsPeriod
        : 0;
  }
  givenPeriodUnitsInMonthPercent() {
    if (!this.appStatistics) return 0.0;
    return this.appStatistics()!.targetUnitsPeriod !== 0
        ? this.appStatistics()!.givenUnitsForPeriodContractsInMonth /
        this.appStatistics()!.targetUnitsPeriod
        : 0;
  }
}

