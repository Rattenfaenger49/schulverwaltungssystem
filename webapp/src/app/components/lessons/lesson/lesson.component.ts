import {Component, ElementRef, inject, OnInit, signal,} from "@angular/core";
import {Device} from "@capacitor/device";
import {
	FormArray,
	FormBuilder,
	FormGroup,
	FormsModule,
	NG_VALUE_ACCESSOR,
	ReactiveFormsModule,
	Validators
} from "@angular/forms";
import {BehaviorSubject, combineLatest, debounceTime, finalize, Observable, of, startWith,} from "rxjs";
import {Student} from "../../../types/student";
import {Teacher} from "../../../types/teacher";
import {UserService} from "../../../_services/data/user.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {InfoMessageType} from "../../../types/Info-message";
import {AuthService} from "../../../_services/data/auth.service";
import {StudentService} from "../../../_services/data/student.service";
import {TeacherService} from "../../../_services/data/teacher.service";
import {DialogService} from "../../../_services/dialog.service";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {AsyncPipe, DatePipe} from "@angular/common";
import {LessonService} from "../../../_services/data/lesson.service";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {
	MatDatepicker,
	MatDatepickerInput,
	MatDatepickerModule,
	MatDatepickerToggle
} from "@angular/material/datepicker";
import {MatSelect} from "@angular/material/select";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {SignatureDialogComponent} from "../../dialogs/signature-dialog/signature-dialog.component";
import {InputDateComponent} from "../../shared/input-date/input-date.component";
import {UploadFileComponent} from "../../shared/upload-file/upload-file.component";
import {MatIconButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import {FileMetadata, FileUploadMetadata} from "../../../types/FileMetadata";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {Lesson} from "../../../types/lesson";
import {FileOpener} from "@capawesome-team/capacitor-file-opener";
import {ModuleType} from "../../../types/enums/module-type";
import {ContractType} from "../../../types/enums/ContractType";
import {NgxMatDatetimePickerModule} from "@angular-material-components/datetime-picker";
import {invokeBlurOnInvalidFormControllers, toLocalDate} from "../../../_services/utils";
import {ResponseService} from "../../../_services/ResponseService";
import {FileCategory} from "../../../types/enums/FileCategory";
import {FileService} from "../../../_services/data/file.service";
import {ToastrService} from "../../../_services/ToastrService";
import {Role} from "../../../types/enums/role";
import {numberInput} from "../../../validators/custom-validations";
import {StudentsLesson} from "../../../types/StudentsLesson";
import {DucumentPageComponent} from "../../shared/ducument-page/ducument-page.component";
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {LessonHistoryComponent} from "../lesson-history/lesson-history.component";
import {UrlParamServiceService} from "../../../_services/url-param-service.service";

@Component({
	selector: "ys-lesson",
	templateUrl: "./lesson.component.html",
	styleUrls: ["./lesson.component.css"],
	providers: [
		{
			provide: [NG_VALUE_ACCESSOR],
			multi: true,
			useExisting: LessonComponent,
		},
	],
	standalone: true,
	imports: [
		FormsModule,
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatAutocompleteModule,
		MatOptionModule,
		AsyncPipe,
		FormErrorDirective,
		MatDatepickerToggle,
		MatDatepicker,
		MatDatepickerInput,
		MatDatepickerModule,
		MatNativeDateModule,
		MatSelect,
		MatIcon,
		DatePipe,
		InputDateComponent,
		UploadFileComponent,
		MatIconButton,
		MatTooltip,
		TooltipModule,
		LoaderComponent,
		NgxMatDatetimePickerModule,
		DucumentPageComponent,
		MatTab,
		MatTabContent,
		LessonHistoryComponent,
		MatTabGroup,
	],
})
// TODO $ convert to signals
export class LessonComponent implements OnInit {
	
	params = signal<Params>([]);
	uploadedFiles = signal<Partial<FileMetadata>[]>([]);
	isUpdate = signal(false);
	metadata = signal<FileUploadMetadata | null>(null);
	isSubmitted = signal<boolean>(false);
	
	form!: FormGroup;
	teachersFilter$: BehaviorSubject<string> = new BehaviorSubject("");
	studentsFilter$: BehaviorSubject<string> = new BehaviorSubject("");
	teachers$?: Observable<Teacher[]>;
	studentsList$!: Observable<Student[]>;
	teacherName = "";
	unitsList: string[] = Array.from({length: 16}, (_, i) =>
		((i + 1) * 0.5).toString()
	);

	private disabledDates?: any[];
	responseService = inject(ResponseService);
	fileStorage = inject(FileService);
	fb = inject(FormBuilder);
	userService = inject(UserService);
	lessonService = inject(LessonService);
	studentService = inject(StudentService);
	teacherService = inject(TeacherService);
	route = inject(ActivatedRoute);
	router = inject(Router);
	auth = inject(AuthService);
	dialog = inject(MatDialog);
	diaService = inject(DialogService);
	el = inject(ElementRef);
	toastr = inject(ToastrService);
	urlParamService = inject(UrlParamServiceService);
	
	constructor() {
		this.route.queryParams.subscribe(params => {
			this.params.set(params);
		})
		
		const lesson = this.route.snapshot?.data["lesson"]?.data;
		this.isUpdate.set(!!lesson);
		this.initializeForm(lesson);
		if (this.isUpdate()) {
			this.metadata.set({
				id: lesson.id, fileCategory: FileCategory.HOMEWORK
			});
		}
		
	}
	
	ngOnInit(): void {
		this.fetchInitialData();
	}
	
	private fetchInitialData() {
		if (this.auth.isAdmin()) {
			this.teacherService.getTeachersFullname().subscribe((res) => {
				this.teachers$ = this.createFilteredTeachersObservable(res.data);
			});
		}
		if (
			this.auth.isAdmin() ||
			this.auth.isTeacher()
		) {
			this.studentService.getStudentsFullname().subscribe((res) => {
				this.studentsList$ = this.createFilteredStudentsObservable(res.data);
			});
			this.fetchDisabledDates();
		}
	}
	// TODO there is better option like saving the hoildays in an Array or fethc the dates from the original url not from our API!
	// TODO PLUS chache the result
	private async fetchDisabledDates() {
		this.userService.getHolidays().subscribe({
			next: (res: any) => {
				this.disabledDates = res.data["feiertage"];
			},
			error: (err) => {
				console.error(err);
			},
		});
	}
	
	private createFilteredTeachersObservable(
		teachers: Teacher[]
	): Observable<Teacher[]> {
		return combineLatest([
			this.teachersFilter$.pipe(startWith("")),
			of(teachers),
		]).pipe(
			debounceTime(200),
			map(([filter, teacherList]) => {
				if (!filter) {
					return teacherList;
				}
				const filterValue = filter.toLowerCase();
				return teacherList.filter((teacher) =>
					(teacher?.firstName + " " + teacher?.lastName)
						.toLowerCase()
						.includes(filterValue)
				);
			})
		);
	}
	
	private createFilteredStudentsObservable(
		students: Student[]
	): Observable<Student[]> {
		return combineLatest([
			this.studentsFilter$.pipe(startWith("")),
			of(students),
		]).pipe(
			debounceTime(300),
			map(([filter, studentList]) => {
				if (!filter) {
					return studentList.filter((student) => !this.isSelected(student?.id));
				}
				const filterValue = filter.toLowerCase();
				
				return studentList.filter(
					(student) =>
						!this.isSelected(student?.id) &&
						(student?.firstName + " " + student?.lastName)
							.toLowerCase()
							.includes(filterValue)
				);
			})
		);
	}
	
	onSave() {
		this.isSubmitted.set(true);
		if (this.form.invalid || this.form.pristine) {
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern");
			
			return;
		}
		
		if (this.form.value.id === null) {
			this.createLesson(this.form.value);
		} else {
			this.updateLesson(this.form.value);
		}
	}
	
	get students(): FormArray {
		return this.form.controls["students"] as FormArray;
	}
	
	private isSelected(studentId: number): boolean {
		return this.students.controls.some(
			(control) => control.value.id === studentId
		);
	}
	
	assignStudent(student: Student) {
		// TODO check if correct
		const studentForm = this.fb.group({
			...student,
		});
		this.students.push(studentForm);
		this.form.markAsDirty();
	}
	
	onAssignTeacher(teacher: Teacher) {
		this.form.patchValue({
			teacher: teacher,
		});
		this.form.markAsDirty();
	}
	
	removeStudent(index: number) {
		this.students.removeAt(index);
		this.form.markAsDirty();
	}
	
	isDateEnabled = (date: Date) => {
		if (!date) return;
		const dayOfWeek = date?.getDay();
		const selectedDate = this.formatDate(date);
		return (
			dayOfWeek !== 0 &&
			!this.disabledDates?.some((holiday) => {
				return holiday.date === selectedDate;
			})
		);
	};
	updated: boolean = false;
	
	filterTeachers(value: string): void {
		this.teachersFilter$.next(value);
	}
	
	filterStudents(value: string): void {
		this.studentsFilter$.next(value);
	}
	
	formatDate(date: Date): string {
		const year = date.getFullYear();
		const month = this.padZero(date.getMonth() + 1);
		const day = this.padZero(date.getDate());
		return `${year}-${month}-${day}`;
	}
	
	private padZero(num: number): string {
		return num < 10 ? "0" + num : num.toString();
	}
	
	protected readonly InfoMessageType = InfoMessageType;
	
	
	delete() {
		const msg =
			"Sind Sie Sicher, dass Sie dieser Unterricht löschen möchten?\n" +
			"Bitte beachten Sie, dass Diese Aktion nicht rückgängig gemacht werden kann! ";
		this.diaService.confirmationDialog(msg).subscribe(
			(res: any) => {
				if (res) {
					this.lessonService.deleteLesson(this.form.value.id).subscribe({
						next: (res) => {
							this.toastr.success("", "Unterricht wurde gelöscht");
							this.router.navigateByUrl("/lessons");
							
						},
						error: (err) => {
							console.error(err);
						},
					});
				}
			},
			(err: any) => console.error(err)
		);
	}
	
	
	private updateLesson(lesson: Lesson) {

		
		this.lessonService
			.updateLseeon(lesson)
			.subscribe({
				next: (res) => {
					this.updated = true;
					this.responseService.responseDialog(res, true);
					this.form.value.isSigned = false;
					this.form.markAsPristine();
					
				},
				error: (err) => {
					console.error(err);
				},
			});
	}
	
	createLesson(lesson: Lesson) {
		this.lessonService
			.createLesson(lesson)
			.pipe(
				finalize(() => {
					this.form.markAsPristine();
				})
			)
			.subscribe({
				next: (res) => {
					this.updated = true;
					this.responseService.responseDialog(res, true);
					this.form.get("id")?.setValue(res.data.id);
					this.form.value.isSigned = false;
					this.isUpdate.set(true);
					this.metadata.set({
						id: res.data.id, fileCategory: FileCategory.HOMEWORK
					});
				},
				error: (err) => {
					console.error(err);
				},
			});
	}
	
	private initializeForm(lesson: Lesson) {
		
		this.form = this.fb.group({
			id: [lesson?.id ?? null],
			modulType: [lesson?.modulType ?? null, Validators.required],
			startAt: [toLocalDate(lesson?.startAt), Validators.required],
			units: [lesson?.units.toString() ?? null, [Validators.required, numberInput()]],
			lessonType: [lesson?.lessonType ?? null, Validators.required],
			contractType: [lesson?.contractType ?? null, Validators.required],
			description: [lesson?.description ?? null, Validators.required],
			comment: [lesson?.comment ?? null],
			students: this.fb.array([], Validators.required),
			isSigned: [lesson?.isSigned ?? false, Validators.required],
			teacher: [lesson?.teacher ?? null, Validators.required],
		});
		lesson?.studentsLesson.forEach((studentsLesson: StudentsLesson) => {
			this.assignStudent(studentsLesson.student);
		});
		if (lesson?.teacher != null)
			this.teacherName = lesson.teacher.firstName + ' ' + lesson.teacher.lastName;
		
		this.uploadedFiles.set(lesson?.files ?? []);
		
		if (this.auth.isAdmin()) {
			this.form.addControl(
				"teacher",
				this.fb.control(lesson?.teacher ?? null, Validators.required)
			);
		}
	}
	
	openSignatureDialog(): void {
		const dialogRef = this.dialog.open(SignatureDialogComponent);
		
		dialogRef.afterClosed().subscribe((signature) => {
			if (signature) {
				// Handle the signature data here
				this.lessonService
					.addSignature({lessonId: this.form.value.id, signature: signature})
					.subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.form.value.isSigned = true;
						},
						error: (err) => {
							console.error(err);
						},
					});
			}
		});
	}
	
	fileUploaded(file: FileMetadata | null) {
		if (file === null) {
			// Alert the User that the file was not uploaded
			return;
		}
		this.uploadedFiles.set([
			...this.uploadedFiles(),
			{id: file.id, fileName: file.fileName, fileCategory: file.fileCategory},
		]);
		this.toastr.success("Hochladen", "Datei wurde hochgeladen!");
	}
	
	
	async downloadFileOnDevice(file: Partial<FileMetadata>) {
		const device = await Device.getInfo();
		this.fileStorage
			.getLessonFile(file)
			.subscribe({
				next: async (res) => {
					if (device.platform !== "web") {
						await this.openOnMobile(file.fileName, res.data);
					} else {
						const byteCharacters = atob(res.data);
						const byteNumbers = new Array(byteCharacters.length);
						for (let i = 0; i < byteCharacters.length; i++) {
							byteNumbers[i] = byteCharacters.charCodeAt(i);
						}
						const byteArray = new Uint8Array(byteNumbers);
						const blob = new Blob([byteArray], {type: file.fileCategory});
						const url = window.URL.createObjectURL(blob);
						window.open(url);
					}
				},
				error: (err) => {
					console.error(err);
					this.toastr.error("Datei Herunterladen", "Fehler bei Datei Schreiben!");
					
				},
			});
	}
	
	deleteFile(file: Partial<FileMetadata>) {
		this.fileStorage.deleteFile(file.id).subscribe({
			next: res => {
				this.uploadedFiles.set(
					this.uploadedFiles().filter((f) => f.id !== file.id)
				);
				this.responseService.responseDialog(res, true);
			}
		});
		
	}
	
	
	protected readonly ContractType = ContractType;
	protected readonly Object = Object;
	protected readonly ModuleType = ModuleType;
	
	private async openOnMobile(fileName: string | undefined, fileData: string | Blob) {
		try {
			
			const filePath = `/${fileName}`;
			
			const status = await Filesystem.checkPermissions();
			if (status.publicStorage !== 'granted') {
				const permissionStatus = await Filesystem.requestPermissions();
				if (permissionStatus.publicStorage !== 'granted') {
					this.toastr.error("", "Berechtigung zum Schreiben von Dateien fehlt");
					return;
				}
			}
			
			await Filesystem.writeFile({
				path: filePath,
				data: fileData,
				directory: Directory.Documents,
			});
			const uri = await Filesystem.getUri({
				directory: Directory.Documents,
				path: filePath,
			});
			
			await FileOpener.openFile({
					path: uri.uri,
					mimeType: "application/pdf",
				})
				.then(() => console.debug("File opened"))
				.catch((error) => console.error("Error opening file", error));
		} catch (e) {
			console.error("Unable to write file", e);
			this.toastr.error("Datei Herunterladen", "Fehler bei Datei Schreiben!");
			
			
		}
	}
	
	protected readonly Role = Role;
	

}


