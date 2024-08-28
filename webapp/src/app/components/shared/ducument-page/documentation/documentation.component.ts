import {Component, ElementRef, inject, input, OnInit} from '@angular/core';
import {ContractType} from "../../../../types/enums/ContractType";
import {FormErrorDirective} from "../../../../_services/directivs/form-error-directive";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ResponseService} from "../../../../_services/ResponseService";
import {AuthService} from "../../../../_services/data/auth.service";
import {UserService} from "../../../../_services/data/user.service";
import {invokeBlurOnInvalidFormControllers, openFile} from "../../../../_services/utils";
import {LoadingService} from "../../../../_services/LoadingService";
import {Person} from "../../../../types/person";
import {ToastrService} from "../../../../_services/ToastrService";
import {MatDatepickerModule, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatFormFieldModule, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {NgxMatDatetimePickerModule} from "@angular-material-components/datetime-picker";
import {MatIcon} from "@angular/material/icon";
import {MAT_DATE_FORMATS, MatOption} from '@angular/material/core';
import {MatSelect} from "@angular/material/select";
import {MatButton, MatFabButton} from "@angular/material/button";

const MY_FORMATS = {
	parse: {
		dateInput: 'DD/MM/yyyy',
	},
	display: {
		dateInput: 'DD/MM/yyyy',
		monthYearLabel: 'DD MMM YYYY',
		dateA11yLabel: 'LL',
		monthYearA11yLabel: 'DDDD MMMM YYYY',
	},
};

@Component({
	selector: 'ys-documentation',
	standalone: true,
	imports: [
		FormErrorDirective,
		ReactiveFormsModule,
		MatDatepickerToggle,
		MatSuffix,
		MatFormFieldModule, MatDatepickerModule, MatInput, NgxMatDatetimePickerModule, MatIcon, MatSelect, MatOption, MatButton, MatFabButton
	],
	providers: [
		{provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
	],
	templateUrl: './documentation.component.html',
	styleUrl: './documentation.component.scss'
})
export class DocumentationComponent implements OnInit {
	protected readonly ContractType = ContractType;
	protected readonly Object = Object;
	
	form!: FormGroup;
	user = input.required<Person>();
	
	
	responseService = inject(ResponseService);
	fb = inject(FormBuilder);
	auth = inject(AuthService);
	userService = inject(UserService);
	loadingService = inject(LoadingService);
	el = inject(ElementRef);
	
	constructor() {
	
	
	}
	
	ngOnInit(): void {
		this.form = this.fb.group({
			start: [null, [Validators.required]],
			end: [null, [Validators.required]],
			dateRange: [null],
			userId: [this.user().id],
			contractType: ['Alle', [Validators.required]],
		});
		
	}
	
	
	toastr = inject(ToastrService);
	
	async createPdf() {
		if (this.form.invalid) {
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern");
			
			return;
		}
		const start = this.form.get('start')?.value;
		const end = this.form.get('end')?.value;
		
		const startDate = new Date(start);
		const endDate = new Date(end);
		const request = {...this.form.value};
		request.start = startDate.toISOString().split('T')[0];
		request.end = endDate.toISOString().split('T')[0];
		
		
		this.userService
			.createPDF(request)
			.subscribe({
				next: async (res) => {
					
					this.loadingService.loadingOn();
					await openFile(res.data, "dokumentation-" + new Date().toISOString().slice(0, 10));
					this.loadingService.loadingOff();
					
				},
				error: (err) => {
					console.error(err);
				},
			});
	}
	
	
}
