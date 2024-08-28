import {HttpErrorResponse} from '@angular/common/http';
import {Component, inject, OnInit, signal} from '@angular/core';
import {SystemlHealth} from "../../types/system-health";
import {SystemCPU} from "../../types/system-CPU";
import {DashboardService} from "../../_services/dashboard.service";
import {DatePipe, NgClass} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {TraceDetailsDialogComponent} from "../dialogs/trace-details-dialog/trace-details-dialog.component";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {Trace} from "../../types/Trace";
import {filter} from "rxjs";


@Component({
	selector: 'ys-monitoring-dashboard',
	templateUrl: './monitoring-dashboard.component.html',
	standalone: true,
	imports: [
		NgClass,
		DatePipe,
		MatIcon
	],
	styleUrls: ['./monitoring-dashboard.component.scss']
})
export class MonitoringDashboardComponent implements OnInit {
	tracesList = signal<Trace[]>([]);
	schownList = signal<Trace[]>([]);
	
	systemHealth!: SystemlHealth;
	systemCPU?: SystemCPU;
	processUptime!: string;
	http200Traces = signal<Trace[]>([]);
	http400Traces = signal<Trace[]>([]);
	http404Traces = signal<Trace[]>([]);
	http500Traces = signal<Trace[]>([]);
	httpDefaultTraces = signal<Trace[]>([]);
	timeStamp!: number;
	private dialog = inject(MatDialog)
	
	constructor(private dashboardService: DashboardService) {
	}
	
	ngOnInit(): void {
		this.getTraces();
		this.getCpuUsage();
		this.getSystemHealth();
		this.getProcessUpTime(true);
	}
	
	private getCpuUsage(): void {
		this.dashboardService.getSystemCPU().subscribe(
			(response: SystemCPU) => {
				this.systemCPU = response;
			},
			(error: HttpErrorResponse) => {
				alert(error.message);
			}
		);
	}
	
	private getSystemHealth(): void {
	/*	this.dashboardService.getSystemHealth().subscribe(
			(response: SystemlHealth) => {
				this.systemHealth = response;
				this.systemHealth.components.diskSpace.details.free = this.formatBytes(this.systemHealth.components.diskSpace.details.free);
			}
		);*/
	}
	
	private getProcessUpTime(isUpdateTime: boolean): void {
		this.dashboardService.getProcessUptime().subscribe(
			(response: any) => {
				this.timeStamp = Math.round(response.measurements[0].value);
				this.processUptime = this.formateUptime(this.timeStamp);
				if (isUpdateTime) {
					this.updateTime();
				}
				
			}
		);
	}
	
	private getTraces(): void {
		
		this.dashboardService.getHttpTraces().subscribe(
			(response: any) => {
				this.processTraces(response.exchanges);
			}
		);
		
		
	}
	
	private processTraces(traces: Trace[]): void {
		this.tracesList.set(traces);
		this.schownList.set(this.tracesList());
		let list200: Trace[] = [];
		let list404: Trace[] = [];
		let list400: Trace[] = [];
		let list500: Trace[] = [];
		let listdefault: Trace[] = [];
		this.tracesList().forEach(trace => {
			switch (trace.response.status) {
				case 200:
					list200.push(trace);
					break;
				case 400:
					list400.push(trace);
					break;
				case 500:
					list500.push(trace);
					break;
				case 404:
					list404.push(trace);
					break;
				default:
					listdefault.push(trace);
					break;
			}
			
		});
		this.http200Traces.set(list200);
		this.http400Traces.set(list400);
		this.http404Traces.set(list404);
		this.http500Traces.set(list500);
		this.httpDefaultTraces.set(listdefault);
	}
	
	onRefreshData() {
		this.http200Traces.set([]);
		this.http400Traces.set([]);
		this.http404Traces.set([]);
		this.http500Traces.set([]);
		this.getTraces();
		this.getCpuUsage();
		this.getSystemHealth();
		this.getProcessUpTime(false);
	}
	
	onSelectTrace(trace: Trace) {
		
		const dialogConfig = new MatDialogConfig();
		
		dialogConfig.data = {trace: trace};
		// Open thie Dialog with the Configuration
		const dialogRef = this.dialog.open(TraceDetailsDialogComponent, dialogConfig);
		
		
	}
	
	formatBytes(bytes: any): string {
		if (bytes === 0) {
			return "0 Bytes";
		}
		const k = 1024;
		const dm = 2 < 0 ? 0 : 2;
		const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
		const i = Math.floor(Math.log(bytes) / Math.log(k));
		return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
	}
	
	formateUptime(timestamp: number): string {
		const hours = Math.floor(timestamp / 60 / 60);
		const minutes = Math.floor(timestamp / 60) - (hours * 60);
		const seconds = timestamp % 60;
		return hours.toString().padStart(2, '0') + 'h' +
			minutes.toString().padStart(2, '0') + 'm' + seconds.toString().padStart(2, '0') + 's';
	}
	
	// every 1 second call this function
	updateTime(): void {
		setInterval(() => {
			this.processUptime = this.formateUptime(this.timeStamp + 1);
			this.timeStamp++;
		}, 1000);
	}
	
	private formatDate(date: Date): string {
		const dd = date.getDate();
		const mm = date.getMonth() + 1;
		const year = date.getFullYear();
		if (dd < 10) {
			const day = `0${dd}`;
		}
		if (mm < 10) {
			const month = `0${mm}`;
		}
		return `${mm}/${dd}/${year}`;
	}
	
	protected readonly filter = filter;
	
	onFilterByStatus(list: any) {
		this.schownList.set(list);
		
	}
}
