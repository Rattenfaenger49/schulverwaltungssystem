import {ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, OnInit, signal} from "@angular/core";
import {Teacher} from "../../../types/teacher";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators,} from "@angular/forms";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {TeacherService} from "../../../_services/data/teacher.service";
import {Student} from "../../../types/student";
import {CustomDialogComponent, DialogData,} from "../../dialogs/custom-dialog/custom-dialog.component";
import {regexValidator, ValidationPatterns} from "../../../validators/custom-validations";
import {DialogService} from "../../../_services/dialog.service";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {AddressComponent} from "../../shared/address/address.component";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {HttpClientXsrfModule} from "@angular/common/http";
import {MatTooltip} from "@angular/material/tooltip";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {AuthService} from "../../../_services/data/auth.service";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {StudentsListComponent} from "../../students/students-list/students-list.component";
import {MatInput} from "@angular/material/input";
import {ResponseService} from "../../../_services/ResponseService";
import {DucumentPageComponent} from "../../shared/ducument-page/ducument-page.component";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {ToastrService} from "../../../_services/ToastrService";
import {UrlParamServiceService} from "../../../_services/url-param-service.service";
import {CalenderComponent} from "../../calender/calender.component";
import {Role} from "../../../types/enums/role";
import {BankDataComponent} from "../../shared/bank-daten/bank-daten.component";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";

@Component({
	selector: "ys-teacher",
	templateUrl: "./teacher.component.html",
	styleUrls: ["./teacher.component.scss"],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		FormsModule,
		ReactiveFormsModule,
		FormErrorDirective,
		AddressComponent,
		MatIconModule,
		MatButtonModule,
		HttpClientXsrfModule,
		MatTooltip,
		NgOptimizedImage,
		TooltipModule,
		LoaderComponent,
		MatTab,
		MatTabContent,
		MatTabGroup,
		StudentsListComponent,
		NgIf,
		MatInput,
		DucumentPageComponent,
		CalenderComponent,
		BankDataComponent,
		MatChipSet,
		MatChip,
		CdkTextareaAutosize,
	],
})
export class TeacherComponent implements OnInit {
	teacher = signal<Teacher|null>(null);
	form!: FormGroup;
	formAssignStudents!: FormGroup;
	params = signal<Params>([]);
	urlParamService = inject(UrlParamServiceService);
	
	private assingdStudents: Student[] = [];
	
	responseService = inject(ResponseService);
	
	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private fb: FormBuilder,
		private diaService: DialogService,
		private teacherService: TeacherService,
		public auth: AuthService,
		private dialog: MatDialog,
	) {
		this.teacher.set(this.route.snapshot.data["user"].data);
		this.route.queryParams.subscribe(params => {
			this.params.set(params);
		})
	}
	
	isInvalid = signal<boolean>(false);
	
	ngOnInit(): void {
		this.form = this.initForm();
		this.formAssignStudents = this.fb.group({students: this.fb.array([])});
		
		this.teacher()?.students.forEach((student: Student) => {
			this.addStudent(student);
		});
	}
	
	initForm(): FormGroup<any> {
		return this.fb.group({
			id: [{value: this.teacher()?.id, disabled: false}, Validators.required],
			gender: [this.teacher()?.gender, Validators.required],
			firstName: [
				{value: this.teacher()?.firstName, disabled: false},
				[Validators.required, Validators.minLength(3)],
			],
			lastName: [
				{value: this.teacher()?.lastName, disabled: false},
				Validators.required,
			],
			birthdate: [this.teacher()?.birthdate, [Validators.required]],
			phoneNumber: [
				this.teacher()?.phoneNumber,
				[
					Validators.required,
					Validators.pattern(ValidationPatterns.phoneNumber),
				],
			],
			username: [
				{value: this.teacher()?.username, disabled: false},
				[Validators.required, Validators.pattern(ValidationPatterns.email)],
			],
			qualifications: [this.teacher()?.qualifications],
			education: [this.teacher()?.education],
			comment: [this.teacher()?.comment],
			singleLessonCost: [this.teacher()?.singleLessonCost?.toFixed(2), [regexValidator(/^[0-9]+(\.[0-9]{2})?$/)]],
			groupLessonCost: [this.teacher()?.groupLessonCost?.toFixed(2), [regexValidator(/^[0-9]+(\.[0-9]{2})?$/)]],
			address: [{value: this.teacher()?.address, disabled: false}],
			taxId: [this.teacher()?.taxId, [regexValidator(/^DE\d{10,11}$/)]],
		});
	}
	
	addStudent(student: Student) {
		const studentForm = this.fb.group({
			...student,
		});
		this.students.push(studentForm);
	}
	
	get students() {
		return this.formAssignStudents?.controls["students"] as FormArray;
	}
	
	el = inject(ElementRef);
	toastr = inject(ToastrService);
	
	onSubmitInfo() {
		
		
		if (this.form.invalid || this.form.pristine) {
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			this.isInvalid.set(true);
			
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern");
			return;
		}
		
		
		this.teacherService
			.updateTeacher(this.form.value)
			.subscribe({
				next: (res) => {
					this.form.markAsPristine();
					this.responseService.responseDialog(res, true);
				},
				error: (err) => {
					console.error(err);
				},
			});
	}
	
	onSubmitAssignment(student: Student) {
		this.teacherService
			.assignStudentToTeacher(this.form.value.id, student.id)
			.subscribe({
				next: (res) => {
					this.addStudent(student);
					this.responseService.responseDialog(res, true);
				},
				error: (err) => {
					console.error(err);
				},
			});
	}
	
	openAssignmetDialog() {
		this.diaService.openAssignDialog().afterClosed().subscribe({
			next: (res) => {
				if (!res) return;
				this.onSubmitAssignment(res);
			},
			error: (err) => console.error(err),
		});
		
	}
	
	removeStudentAssignment(index: number) {
		const msg = "Sind Sie sicher, dass Sie diese Bezieung löschen möchten?";
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					const studentId = this.students.at(index).get("id")?.value;
					this.removeIfExist(studentId);
					this.students.removeAt(index);
					this.teacherService
						.removeStudentAssignment(this.form.value.id, studentId)
						.subscribe();
				}
			},
			error: (err: any) => console.error(err),
		});
	}
	
	removeIfExist(studentIdToRemove: number) {
		this.assingdStudents = this.assingdStudents.filter(
			(student) => student.id !== studentIdToRemove,
		);
	}
	
	
	delete() {
		const msg = "Sind Sie Sicher, dass Sie dem Benutzer löschen möchten?\n" +
			"Bitte beachten Sie, dass der Benutzer zur Löschung markiert wird und erst nach 3 Monaten endgültig gelöscht wird!"
		;
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					this.teacherService.deleteTeacher(this.form.value.id).subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.teacher.set(res.data);
							this.form.patchValue({...res.data});
							
						},
						error: (err) => {
							console.error(err);
						},
					});
				}
			},
			error: (err: any) => console.error(err),
		});
	}
	
	undoDelete() {
		const msg = "Sind Sie Sicher, dass Sie den Benutzer wiederherstellen möchten?";
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					this.teacherService.undoDeleteTeacher(this.form.value.id).subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.teacher.set(res.data);
							this.form.patchValue({...res.data});
						},
						error: (err) => {
							console.error(err);
						},
					});
				}
			},
			error: (err: any) => console.error(err),
		});
	}
	
	
	protected readonly Role = Role;

	
	openBankDialog() {
		this.diaService.openBankDialog(this.teacher()!.id);
	}
}
