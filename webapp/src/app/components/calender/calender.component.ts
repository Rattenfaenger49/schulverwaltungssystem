import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	Inject,
	inject,
	input,
	LOCALE_ID,
	OnInit,
	signal,
	ViewChild,
} from "@angular/core";


import {addMonths, endOfDay} from "date-fns";
import {
	addPeriod,
	CalendarSchedulerEvent,
	CalendarSchedulerEventAction,
	CalendarSchedulerEventStatus,
	CalendarSchedulerViewComponent,
	endOfPeriod,
	SchedulerDateFormatter,
	SchedulerEventTimesChangedEvent,
	SchedulerModule,
	SchedulerViewDay,
	SchedulerViewHour,
	SchedulerViewHourSegment,
	startOfPeriod,
	subPeriod,
} from 'angular-calendar-scheduler';
import {CalendarCommonModule, CalendarDateFormatter, CalendarView, DateAdapter,} from 'angular-calendar';
import {AsyncPipe, DatePipe, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AuthService} from "src/app/_services/data/auth.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {SchedulerDialogComponent} from "../dialogs/schedular/scheduler-dialog.component";
import {MatMomentDateModule} from "@angular/material-moment-adapter";
import {
	MatDatepicker,
	MatDatepickerInput,
	MatDatepickerModule,
	MatDatepickerToggle
} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {parseDateTimeStringFromDate, toLocalDate} from "../../_services/utils";
import {AppointmentService} from "../../_services/data/appointment.service";
import {Appointment} from "../../types/Appointment";
import {DialogService} from "../../_services/dialog.service";
import {BehaviorSubject} from "rxjs";

@Component({
	selector: "ys-calender",
	standalone: true,
	imports: [
		SchedulerModule,
		NgSwitch,
		CalendarCommonModule,
		FormsModule,
		NgIf,
		NgSwitchCase,
		MatDatepickerToggle,
		MatDatepicker,
		MatDatepickerInput,
		MatDatepickerModule,
		MatNativeDateModule,
		MatMomentDateModule,
		DatePipe,
		AsyncPipe,
	],
	providers: [
		{
			provide: CalendarDateFormatter,
			useClass: SchedulerDateFormatter,
		},
	],
	templateUrl: "./calender.component.html",
	styleUrl: "./calender.component.scss",
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class CalenderComponent implements OnInit {
	title: string = "Angular Calendar Scheduler Demo";
	userId = input<number | null>(null)
	actions: CalendarSchedulerEventAction[] = [
		{
			when: "enabled",
			label:
				'<span class="add-icon"><i class="material-icons md-18 md-green-500">add</i></span>',
			title: "add",
			onClick: (event: CalendarSchedulerEvent): void => {
				this.addAttendees(event);
			},
		},
		
		{
			when: "enabled",
			label:
				'<span class="close-icon"><i class="material-icons md-18 md-red-500">cancel</i></span>',
			title: "Delete",
			onClick: (event: CalendarSchedulerEvent): void => {
				this.deleteEvent(event);
			},
		},
	
	];
	refresh: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
	view = signal<CalendarView>(CalendarView.Week);
	viewDate = signal<Date>(new Date());
	viewDays = signal<number>(7);
	locale: string = "de";
	hourSegments = signal<number>(4);
	weekStartsOn = signal<number>(1);
	startsWithToday: boolean = false;
	activeDayIsOpen: boolean = true;
	excludeDays: number[] = []; // [0];
	dayStartHour: number = 8;
	dayEndHour: number = 22;
	minDate = signal<Date>(new Date(new Date().setFullYear(new Date().getFullYear() - 1)));
	maxDate = signal<Date>(endOfDay(addMonths(new Date(), 6)));
	dayModifier!: Function;
	hourModifier!: Function;
	segmentModifier: Function;
	eventModifier: Function;
	prevBtnDisabled: boolean = false;
	nextBtnDisabled: boolean = false;
	
	events = signal<CalendarSchedulerEvent[]>([]);
	fetchedMonths = signal<Set<string>>(new Set());
	
	@ViewChild(CalendarSchedulerViewComponent)
	calendarScheduler!: CalendarSchedulerViewComponent;
	appointmentService = inject(AppointmentService);
	diaService = inject(DialogService);
	dialog = inject(MatDialog);
	auth = inject(AuthService);
	dateAdapter = inject(DateAdapter);
	cdr = inject(ChangeDetectorRef);
	
	constructor(@Inject(LOCALE_ID) locale: string,) {
		this.locale = locale;
		
		this.dayModifier = ((day: SchedulerViewDay): void => {
			day.cssClass = this.isDateValid(day.date) ? '' : 'cal-disabled';
		}).bind(this);
		
		this.hourModifier = ((hour: SchedulerViewHour): void => {
			hour.cssClass = this.isDateValid(hour.date) ? '' : 'cal-disabled';
		}).bind(this);
		
		this.segmentModifier = ((segment: SchedulerViewHourSegment): void => {
			segment.isDisabled = !this.isDateValid(segment.date);
		}).bind(this);
		
		this.eventModifier = ((event: CalendarSchedulerEvent): void => {
			event.isDisabled = !this.isDateValid(event.start);
		}).bind(this);
		
		
	}
	
	ngOnInit(): void {
		this.dateOrViewChanged(this.viewDate());
	}
	
	viewDaysOptionChanged(viewDays: any): void {
		
		this.viewDays.set(Number(viewDays));
		// this is mendatory by changing the dateview
		// there is better way to determin start and end but  this is easier in this case :-)
		this.fetchEventsForDate(false);
		this.fetchEventsForDate(true);
	}
	
	
	dateOrViewChanged(date: Date): void {
		
		
		const next = this.viewDate() <= date;
		this.viewDate.set(date);
		this.fetchEventsForDate(next);
		if (this.startsWithToday) {
			
			this.prevBtnDisabled = !this.isDateValid(
				subPeriod(
					this.dateAdapter,
					this.view(),
					this.viewDate(),
					1
				)
			);
			this.nextBtnDisabled = !this.isDateValid(
				addPeriod(
					this.dateAdapter,
					this.view(),
					this.viewDate(),
					1
				)
			);
		} else {
			this.prevBtnDisabled = !this.isDateValid(
				endOfPeriod(
					this.dateAdapter,
					this.view(),
					subPeriod(
						this.dateAdapter,
						this.view() /*this.view*/,
						this.viewDate(),
						1
					)
				)
			);
			this.nextBtnDisabled = !this.isDateValid(
				startOfPeriod(
					this.dateAdapter,
					this.view() /*this.view*/,
					addPeriod(
						this.dateAdapter,
						this.view()/*this.view*/,
						this.viewDate(),
						1
					)
				)
			);
		}
		/*
		maybe there is no need
				if (this.viewDate() < this.minDate()) {
			this.changeDate(this.minDate());
		} else if (this.viewDate() > this.maxDate()) {
			this.changeDate(this.maxDate());
			
		}
		 */
		
	}
	
	private isDateValid(date: Date): boolean {
		return /*isToday(date) ||*/ date >= this.minDate() && date <= this.maxDate();
	}
	
	viewDaysChanged(viewDays: number): void {
		this.viewDays.set(viewDays);
		// Manually trigger change detectio, um ein Error zu Vermeiden!!
		this.cdr.detectChanges();
		
	}
	
	dayHeaderClicked(day: SchedulerViewDay): void {
	}
	
	hourClicked(data: SchedulerViewHour): void {
		
		this.openDialogCreate({
			startAt: data.date,
			endAt: data.date
		});
	}
	
	segmentClicked(action: string, segment: SchedulerViewHourSegment): void {
		
		this.openDialogCreate({
			startAt: segment.date,
			endAt: segment.date
		});
	}
	
	openDialogCreate(data: any) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.data = data;
		// Open thie Dialog with the Configuration
		const dialogRef = this.dialog.open(SchedulerDialogComponent, dialogConfig);
		
		// Update Person after closing the popup
		dialogRef.afterClosed().subscribe((data) => {
			if (data) {
				this.appointmentService
					.addAppointment(data, this.userId())
					.subscribe({
						next: (response) => {
							
							const newEvent = this.createEvent(response.data);
							this.events.update(events => {
								events.push(newEvent);
								return events;
							});
							this.refresh.next(true);
							
						}
					});
			}
		});
	}
	
	openDialogEdit(data: any) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.data = data;
		// Open thie Dialog with the Configuration
		const dialogRef = this.dialog.open(SchedulerDialogComponent, dialogConfig);
		
		// Update Person after closing the popup
		dialogRef.afterClosed().subscribe((data) => {
			if (data) {
				this.appointmentService.updateAppointment(data, this.userId())
					.subscribe({
						next: (res) => {
							const updatedEent = this.createEvent(res.data);
							
							const events = this.events()?.filter((e) => e.id !== data.id.toString());
							if (events) {
								this.events.set([...events, updatedEent]);
							}
							this.refresh.next(true);
							
						}
					});
			}
		});
	}
	
	eventClicked(action: string, event: CalendarSchedulerEvent): void {
		const date = parseDateTimeStringFromDate(event.start);
		const date1 = parseDateTimeStringFromDate(event.end);
		
		this.openDialogEdit({
			...event,
			startAt: date.date,
			endAt: date1.date,
		});
	}
	
	eventTimesChanged({
						  event,
						  newStart,
						  newEnd,
						  type,
					  }: SchedulerEventTimesChangedEvent): void {
		// TODO check if correct1 function will works by drag and drop or resize of event
		const eventToChange = this.events()?.find(e => e.id === event.id);
		
		if (eventToChange) {
			eventToChange.start = newStart;
			eventToChange.end = newEnd;
		}
	}
	
	deleteEvent(event: CalendarSchedulerEvent) {
		const msg =
			"Sind Sie Sicher, dass Sie diesen Termin löschen möchten?\n" +
			"Bitte beachten Sie, dass Diese Aktion nicht rückgängig gemacht werden kann!";
		this.diaService.confirmationDialog(msg).subscribe(
			(res: any) => {
				if (res) {
					this.appointmentService.deleteAppointment(event.id).subscribe({
						next: () => {
							this.events.update(events =>
								events.filter((e) => e.id !== event.id)
							)
						}
					});
				}
			},
			(err: any) => console.error(err)
		);

	}
	
	createEvent(appointment: Appointment) {
		return <CalendarSchedulerEvent>{
			id: appointment.id.toString(),
			start: toLocalDate(appointment.startAt),
			end: toLocalDate(appointment.endAt),
			title: appointment.title,
			content: appointment.content,
			color: {primary: "#E0E0E0", secondary: "#EEEEEE"},
			actions: this.actions,
			status: "ok" as CalendarSchedulerEventStatus,
			isClickable: true,
			isDisabled: false,
			draggable: false,
			resizable: {
				beforeStart: false,
				afterEnd: false,
			},
		}
	}
	
	addAttendees(event: CalendarSchedulerEvent) {
		this.diaService.addAttendees({id: event.id, userId: this.userId()});
	}
	
	toCalendarSchedukerEvent(appointments: Appointment[]) {
		const result: CalendarSchedulerEvent[] = [];
		appointments.forEach((appointment: Appointment) => {
			const event = this.createEvent(appointment);
			result.push(event);
		});
		return result;
	}
	
	private fetchEventsForDate(next: boolean) {
		
		let date = this.viewDate().toISOString();
		let firstDay;
		let lastDay;
		
		switch (this.viewDays()) {
			// to fetch appointment from the last month
			case 7:
				firstDay = this.getFirstDayOfWeek(this.viewDate());
				const date2 = new Date(firstDay);
				date2.setDate(date2.getDate() + 6);
				lastDay = date2.toISOString();
				break;
			case 3 :
				firstDay = date;
				const tmp = new Date(firstDay);
				tmp.setDate(tmp.getDate() + 2);
				lastDay = tmp.toISOString();
				break;
			case  1:
				firstDay = date;
				lastDay = date;
				break;
			default:
				firstDay = date;
				lastDay = date;
				break;
			
		}
		const dateToFetch = next ? lastDay : firstDay;
		if (this.fetchedMonths().has(dateToFetch.slice(0, 7))) {
			return;
		}
		this.appointmentService.getAppointments(dateToFetch.split('T')[0], this.userId()).subscribe(
			{
				next: (res) => {
					this.events.update(events => [...events, ...this.toCalendarSchedukerEvent(res.data)]);
					this.fetchedMonths.update(elements => elements.add(date.slice(0, 7)));
					
				}
			}
		);
		
	}
	
	getFirstDayOfWeek(date: Date): string {
		const currentDate = new Date(date); // Make a copy of the date object
		const day = currentDate.getDay(); // Get the current day of the week (0-6, where 0 is Sunday)
		const diff = currentDate.getDate() - day + (day === 0 ? -6 : 1) + 1; // Adjust when day is Monday (+1 for stat on Monday)
		
		currentDate.setDate(diff); // Set the date to the first day of the week
		
		currentDate.setHours(0, 0, 0, 0); // Reset time to midnight
		
		return currentDate.toISOString().split('T')[0];
	}
	
}
