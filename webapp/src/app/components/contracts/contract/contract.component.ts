import {
	ChangeDetectionStrategy, ChangeDetectorRef,
	Component,
	effect,
	ElementRef,
	EventEmitter,
	inject,
	input,
	OnInit,
	output,
	signal
} from "@angular/core";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators,} from "@angular/forms";
import {Router} from "@angular/router";
import {Contract} from "../../../types/contract";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ModulDialogComponent} from "../../dialogs/modul-dialog/modul-dialog.component";
import {ContractService} from "../../../_services/data/contract.service";
import {Modul} from "../../../types/modul";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {ContactComponent} from "../../shared/contact/contact.component";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {AsyncPipe, CurrencyPipe, JsonPipe, NgClass, TitleCasePipe} from "@angular/common";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption,} from "@angular/material/autocomplete";
import {MatError, MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {BehaviorSubject, combineLatest, debounceTime, Observable, of, startWith,} from "rxjs";
import {Student} from "../../../types/student";
import {map} from "rxjs/operators";
import {StatusComponent} from "../../shared/status/status.component";
import {CustomDialogComponent, DialogData} from "../../dialogs/custom-dialog/custom-dialog.component";
import {AuthService} from "../../../_services/data/auth.service";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {ContractType} from "../../../types/enums/ContractType";
import {ResponseService} from "../../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {ToastrService} from "../../../_services/ToastrService";
import {Role} from "../../../types/enums/role";
import {MatCheckbox} from "@angular/material/checkbox";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
	selector: "ys-contract",
	templateUrl: "./contract.component.html",
	styleUrls: ["./contract.component.scss"],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		FormsModule,
		ReactiveFormsModule,
		FormErrorDirective,
		ContactComponent,
		MatIconModule,
		MatButtonModule,
		AsyncPipe,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatFormField,
		MatInput,
		MatLabel,
		MatOption,
		MatError,
		MatFormFieldModule,
		StatusComponent,
		JsonPipe,
		TooltipModule,
		LoaderComponent,
		TitleCasePipe,
		NgClass,
		CurrencyPipe,
		MatCheckbox,
		CdkTextareaAutosize,
		MatSlideToggle,
	],
})
export class ContractComponent implements OnInit {
	studentsFilter$: BehaviorSubject<string> = new BehaviorSubject("");
	
	// NOTE: contract and students are both ot required, but one of them has to be provided
	// if create then studente, else contract
	contract = input<Contract>();
	student = input<Student>({} as Student);
	contractChanged = output<Contract>();
	contractDeleted = output<number>();
	isContactDisabled = signal(false);
	isInvalid = signal(false);
	isCreate = signal(false);
	form!: FormGroup;
	responseService = inject(ResponseService);
	router = inject(Router);
	auth = inject(AuthService);
	fb = inject(FormBuilder);
	dialog = inject(MatDialog);
	contractService = inject(ContractService);
	
	
	constructor(private cdr: ChangeDetectorRef) {
		effect(()=>{
			this.isContactDisabled() ?
				this.form.get('contact')?.disable() :this.form.get('contact')?.enable();
		});
	}
	
	ngOnInit(): void {
		this.isCreate.set(!this.contract());
		const student = this.contract() ?  this.contract()?.student :this.student();
		this.form = this.fb.group({
			id: [this.contract()?.id ?? null],
			contractNumber: [
				this.contract()?.contractNumber ?? null,
				Validators.required,
			],
			referenceContractNumber: [this.contract()?.referenceContractNumber],
			student: [student, [Validators.required]],
			endAt: [this.contract()?.endAt ?? null, Validators.required],
			startAt: [this.contract()?.startAt ?? null, Validators.required],
			contractType: [this.contract()?.contractType ?? null, Validators.required],
			status: [
				this.contract()?.status?.toString() ?? "INACTIVE",
				Validators.required,
			],
			moduls: this.fb.array([]),
			comment: [this.contract()?.comment ?? ""],
			contact: [this.contract()?.contact ?? null],
		});
		this.contract()?.moduls.forEach((modul: Modul) => this.addModulToForm(modul));
		this.isContactDisabled.set(!this.contract()?.contact);
		
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
		
		
		if (this.isCreate()) {
			this.contractService
				.createContract(this.form.value)
				.subscribe({
					next: (res) => {
						this.responseService.responseDialog(res, true);
						this.form.patchValue({
							id: res.data.id,
							moduls: res.data.moduls
						});
						this.contractChanged.emit(res.data);
						this.router.navigate(["/students/",this.form.value?.student?.id]);
						
					},
					error: (err) => {
						console.error(err);
					},
				});
		} else {
			this.contractService
				.updateContract(this.form.value)
				.subscribe({
					next: (res) => {
						this.responseService.responseDialog(res, true);
						
						this.form.patchValue({
							moduls: res.data.moduls
						});
						this.form.markAsPristine();
						this.contractChanged.emit(res.data);
					},
					error: (err) => {
						console.error(err);
					},
				});
		}
		
	}
	
	get status() {
		return this.form.get('status')?.value;
	}
	
	updateModuls(modul: Modul| null = null) {
		const dialogConfig = new MatDialogConfig();
		
		dialogConfig.disableClose = true;
		
		dialogConfig.autoFocus = false;
		dialogConfig.hasBackdrop = true;
		dialogConfig.data = modul;
		const dialogRef = this.dialog.open(ModulDialogComponent, dialogConfig);
		dialogRef.afterClosed().subscribe({
			next: (updatedModul: Modul) => {
				if (updatedModul) {
					// Find the index of the existing Modul in the form array
					const foundModulIndex = this.moduls.controls.findIndex(
						(m) => m.get("modulType")?.value === updatedModul.modulType
					);
					
					if (foundModulIndex !== -1) {
						// Update the existing Modul with new values
						this.moduls.at(foundModulIndex).patchValue(updatedModul);
					} else {
						// If not found, add new Modul to the form array
						this.addModulToForm(updatedModul);
					}
					this.cdr.detectChanges();
					this.form.markAsDirty();
				}
				
				},
			error: (err) => {
				console.error(err);
			},
		});
	}
	
	get moduls() {
		return this.form?.controls["moduls"] as FormArray;
	}
	
	addModulToForm(modul: Modul) {
		const foundModulIndex = this.moduls.controls.findIndex(
			(m) => m.get("modulType")?.value === modul.modulType
		);
		
		if (foundModulIndex !== -1) {
			const foundModul = this.moduls.controls[foundModulIndex];
			const units =
				(parseInt(foundModul.get("units")?.value) || 0) + modul.units;
			foundModul.patchValue({units: units});
		} else {
			const modulForm = this.fb.group({
				...modul,
			});
			this.moduls.push(modulForm);
		}
		this.form.markAsDirty();
		this.cdr.detectChanges();
	}
	
	removeModul(index: number) {
		this.moduls.removeAt(index);
		this.form.markAsDirty();
		
	}
	
	delete() {
		this.openDialog().subscribe({
			next: (res: any) => {
				if (res) {
					this.contractService.deleteContract(this.form.value.id).subscribe({
						next: (res) => {
							this.responseService.responseDialog(res, true);
							this.contractDeleted.emit(this.form.value.id);
							this.router.navigate(["/students/",this.form.value?.student?.id]);
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
	
	openDialog(): EventEmitter<any> {
		/**
		 * Sind Sie sicher, dass Sie diesen Schüler vom Lehrer entfernen möchten?
		 * Bitte beachten Sie, dass diese Aktion die Beziehung zwischen dem Schüler und dem Lehrer trennen wird,
		 * jedoch nicht den Datensatz des Schülers löschen wird.
		 * */
		const data: DialogData = {
			type: "warning",
			message:
				"Sind Sie Sicher, dass Sie diesen Vertrag löschen möchten?\n" +
				"Bitte beachten Sie, dass Diese Aktion  nicht rückgängig gemacht werden kann! ",
		};
		
		const dialogRef = this.dialog.open(CustomDialogComponent, {
			width: "auto",
			maxWidth: "550px",
			data,
		});
		return dialogRef.componentInstance.dialogResult;
	}
	
	
	protected readonly Object = Object;
	protected readonly ContractType = ContractType;
	protected readonly Role = Role;
	
	
}
/*private createFilteredStudentsObservable(
		students: Student[]
	): Observable<Student[]> {
		return combineLatest([
			this.studentsFilter$.pipe(startWith("")),
			of(students),
		]).pipe(
			debounceTime(300),
			map(([filter, studentList]) => {
				const filterValue = filter.toLowerCase();
				
				return studentList.filter((student) =>
					(student.firstName + " " + student.lastName)
						.toLowerCase()
						.includes(filterValue)
				);
			})
		);
	}*/
