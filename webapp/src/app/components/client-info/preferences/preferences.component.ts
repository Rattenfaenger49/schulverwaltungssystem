import {Component, effect, ElementRef, inject, input, OnDestroy, OnInit} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardModule} from "@angular/material/card";
import {MatFormField} from "@angular/material/form-field";
import {MatCheckbox, MatCheckboxModule} from "@angular/material/checkbox";
import {
	AbstractControl,
	ControlValueAccessor, FormBuilder,
	FormsModule,
	NG_VALIDATORS,
	NG_VALUE_ACCESSOR,
	ReactiveFormsModule, ValidationErrors,
	Validator, Validators
} from "@angular/forms";
import {MatButton, MatButtonModule} from "@angular/material/button";

import {HttpClientModule} from "@angular/common/http";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {Subscription} from "rxjs";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";

@Component({
  selector: 'ys-preferences',
  standalone: true,
	imports: [
		MatCard,
		MatCardHeader,
		MatCardContent,
		MatFormField,
		MatCheckbox,
		MatButton,
		HttpClientModule,
		FormsModule,
		MatCardModule,
		MatCheckboxModule,
		MatButtonModule,
		ReactiveFormsModule,
		FormErrorDirective
	],
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			multi: true,
			useExisting: PreferencesComponent,
		},
		{
			provide: NG_VALIDATORS,
			multi: true,
			useExisting: PreferencesComponent,
		},
	],
  templateUrl: './preferences.component.html',
  styleUrl: './preferences.component.scss'
})
export class PreferencesComponent implements ControlValueAccessor, Validator, OnDestroy{

	
	
	el = inject(ElementRef);
	fb = inject(FormBuilder);
	
	required = input<boolean>(true) ;
	form = this.fb.group({
		allowTeacherBillGeneration: [null, [Validators.required]],
		allowStudentBillGeneration: [null, [Validators.required]],
		emailNotificationsForAppointments: [null, [Validators.required]],
		emailNotificationForLessonChanges: [null, [Validators.required]],
		emailNotificationForClientInfoChanges: [null, [Validators.required]],
	});
	onTouched = () => {};
	onChangeSub!: Subscription;
	isInvalid = input.required<boolean >();
	constructor() {
		effect( ()=> {
			if(this.isInvalid())
				invokeBlurOnInvalidFormControllers(this.form, this.el);
		});
	}
	validate(control: AbstractControl): ValidationErrors | null {
		if (this.form.valid) {
			return null;
		}
		return {
			invalidForm: { valid: false, message: "address fields invalid!" },
		};
	}
	registerOnChange(fn: any): void {
		this.onChangeSub = this.form.valueChanges.subscribe(fn);
	}
	
	registerOnTouched(fn: any): void {
		this.onTouched = fn;
	}
	
	setDisabledState(isDisabled: boolean): void {
		if (isDisabled) {
			this.form.disable();
		} else {
			this.form.enable();
		}
	}
	
	writeValue(value: any): void {
		if (value != null) {
			const inputKeys = Object.keys(value);
			const formKeys = Object.keys(this.form.value);
			let obj: any = {};
			for(const key of formKeys){
				if(inputKeys.includes(key)){
					obj[key] = value[key];
				}
			}
			this.form.setValue(obj);
		}
	}
	
	ngOnDestroy(): void {
		this.onChangeSub?.unsubscribe();
	}
	

	

	

}
