import {Component, effect, ElementRef, EventEmitter, inject, OnInit, signal} from '@angular/core';
import {Student} from "../../../types/student";
import {ActivatedRoute, Params, Router, RouterLink} from "@angular/router";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";

import {ValidationPatterns} from "../../../validators/custom-validations";
import {StudentService} from "../../../_services/data/student.service";
import {StatusStyleDirective} from '../../../_services/directivs/status-style-directive';
import {AddressComponent} from '../../shared/address/address.component';
import {FormErrorDirective} from '../../../_services/directivs/form-error-directive';
import {DatePipe, JsonPipe, NgIf, NgOptimizedImage} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {AuthService} from "../../../_services/data/auth.service";
import {CustomDialogComponent, DialogData} from "../../dialogs/custom-dialog/custom-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {ParentComponent} from "../../parent/parent.component";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {ContractComponent} from "../../contracts/contract/contract.component";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {MatInput} from "@angular/material/input";
import {DucumentPageComponent} from "../../shared/ducument-page/ducument-page.component";
import {LessonsListComponent} from "../../lessons/lessons-list/lessons-list.component";
import {CustomListTableComponent} from "../../shared/custom-list-table/custom-list-table.component";
import {ResponseService} from "../../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {ToastrService} from "../../../_services/ToastrService";
import {UrlParamServiceService} from "../../../_services/url-param-service.service";
import {Contract} from "../../../types/contract";
import {Role} from "../../../types/enums/role";
import {MatButton} from "@angular/material/button";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {DialogService} from "../../../_services/dialog.service";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";

@Component({
	selector: "ys-student",
	templateUrl: "./student.component.html",
	styleUrls: ["./student.component.scss"],
	standalone: true,
	imports: [
		FormsModule,
		ReactiveFormsModule,
		FormErrorDirective,
		AddressComponent,
		RouterLink,
		StatusStyleDirective,
		NgOptimizedImage,
		MatIcon,
		ParentComponent,
		LoaderComponent,
		MatTabGroup,
		MatTab,
		MatTabContent,
		DatePipe,
		ContractComponent,
		JsonPipe,
		MatTabLabel,
		TooltipModule,
		MatInput,
		NgIf,
		DucumentPageComponent,
		LessonsListComponent,
		CustomListTableComponent,
		MatButton,
		MatChip,
		MatChipSet,
		CdkTextareaAutosize
	]
})
export class StudentComponent implements OnInit {
	student = signal<Student | null>(null);
	form!: FormGroup;
	isInvalid = signal<boolean>(false);
	params = signal<Params>([]);
	isContactDisabled = signal(false);
	
	protected urlParamService = inject(UrlParamServiceService);
	responseService = inject(ResponseService);
	route = inject(ActivatedRoute);
	router = inject(Router);
	fb = inject(FormBuilder);
	auth = inject(AuthService);
	dialog = inject(MatDialog);
	studentService = inject(StudentService);
	diaService = inject(DialogService);
	
	
	
	constructor() {
		this.student.set(this.route.snapshot.data["user"].data);
		
		this.route.queryParams.subscribe(p => {
			this.params.set(p);
		});
		effect(()=>{
			this.isContactDisabled() ?
				this.form.get('parent')?.disable() :this.form.get('parent')?.enable();
		
		});
		
	}
	
	ngOnInit(): void {
		this.form = this.initForm();
		
		this.isContactDisabled.set(!this.student()?.parent);
		
	}
	
	el = inject(ElementRef);
	toastr = inject(ToastrService);
	
	onSubmit() {
		if (this.form.invalid || this.form.pristine) {
			this.isInvalid.set(true);
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern",);
			return;
		}
		
		this.studentService
			.updateStudent(this.form?.value)
			.subscribe({
				next: (res) => {
					this.form.markAsPristine();
					this.responseService.responseDialog(res, true);
				}
			});
	}
	
	
	delete() {
		this.openDialog().subscribe({
			next: (res: any) => {
				if (res) {
					this.studentService.deleteStudent(this.student()!.id).subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.student.set(res.data);
							this.form.patchValue({...res.data});
						}
					});
				}
			},
			error: (err: any) => console.error(err),
		});
	}
	
	openDialog(): EventEmitter<any> {
		/**
		 * Sind Sie sicher, dass Sie diesen Schüler vom Lehrer entfernen möchten?
		 * Bitte beachten Sie, dass diese Aktion die Beziehung zwischen dem Schüler und dem Lehrer trennen wird,
		 * jedoch nicht den Datensatz des Schülers löschen wird.
		 * */
		const data: DialogData = {
			type: "warning",
			message:
				"Sind Sie Sicher, dass Sie der Benutzer löschen möchten?\n" +
				"Bitte beachten Sie, dass der Benutzer zur Löschung markiert wird und erst nach 3 Monaten endgültig gelöscht wird!",
		};
		
		const dialogRef = this.dialog.open(CustomDialogComponent, {
			width: "auto",
			maxWidth: "550px",
			data,
		});
		return dialogRef.componentInstance.dialogResult;
	}
	

	
	updateContract(updatedContract: Contract) {
		const contracts: Contract[] = this.form.get('contracts')?.value;
		const filterdContracts = contracts.filter(contract => contract.id !== updatedContract.id)
		this.form.get('contracts')?.setValue([...filterdContracts, updatedContract]);
		
	}
	
	onRowClicked(element: any): void {
		this.router.navigate(['teachers', element.id]);
		
	}
	
	
	removeCotract(id: number) {
		const contracts: Contract[] = this.form.get('contracts')?.value;
		const filterdContracts  = contracts.filter(contract => contract.id !== id);
		this.form.get('contracts')?.setValue([...filterdContracts]);

	}
	
	protected readonly Role = Role;
	
	resendConfirmationEmail() {
		this.auth.resendConfirmationEmail(this.student()!.id).subscribe({
			next: (res) => {
				this.toastr.success("", "Email wurde erneut gesendet!");
			}
		});
	
	}
	
	openBankDialog() {
		this.diaService.openBankDialog(this.form.value!.id);
	}
	
	undoDelete() {
		const msg = "Sind Sie Sicher, dass Sie den Benutzer wiederherstellen möchten?";
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					this.studentService.undoDeleteStudent(this.form.value.id).subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.student.set(res.data);
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
	
	private initForm() {
		return this.fb.group({
			id: [this.student()?.id],
			gender: [this.student()?.gender],
			firstName: [
				{value: this.student()?.firstName, disabled: false},
				[Validators.required, Validators.minLength(2)],
			],
			lastName: [
				{value: this.student()?.lastName, disabled: false},
				[Validators.required, Validators.minLength(2)],
			],
			birthdate: [this.student()?.birthdate, [Validators.required]],
			phoneNumber: [
				this.student()?.phoneNumber,
				[
					Validators.required,
					Validators.pattern(ValidationPatterns.phoneNumber),
				],
			],
			username: [
				{value: this.student()?.username, disabled: false},
				[Validators.required, Validators.pattern(ValidationPatterns.email)],
			],
			level: [this.student()?.level, Validators.required],
			comment: [this.student()?.comment],
			address: [this.student()?.address],
			contracts: [this.student()?.contracts],
			parent: [this.student()?.parent],
		});
	}
}

