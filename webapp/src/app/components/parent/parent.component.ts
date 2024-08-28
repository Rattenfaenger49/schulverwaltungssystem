import {Component, effect, ElementRef, inject, input, model, OnDestroy} from '@angular/core';
import {
	AbstractControl,
	ControlValueAccessor,
	FormBuilder,
	NG_VALIDATORS,
	NG_VALUE_ACCESSOR,
	ReactiveFormsModule,
	ValidationErrors,
	Validator,
	Validators,
} from "@angular/forms";
import {FormErrorDirective} from "../../_services/directivs/form-error-directive";
import {Subscription} from "rxjs";
import {ValidationPatterns} from "../../validators/custom-validations";
import {invokeBlurOnInvalidFormControllers} from "../../_services/utils";
import {NgOptimizedImage} from "@angular/common";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
	selector: "ys-parent",
	standalone: true,
	imports: [ReactiveFormsModule, FormErrorDirective, NgOptimizedImage, MatSlideToggle],
	templateUrl: "./parent.component.html",
	styleUrl: "./parent.component.css",
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			multi: true,
			useExisting: ParentComponent,
		},
		{
			provide: NG_VALIDATORS,
			multi: true,
			useExisting: ParentComponent,
		},
	],
})
export class ParentComponent
	implements ControlValueAccessor, Validator, OnDestroy {
	el = inject(ElementRef);
	fb = inject(FormBuilder);
	
	form = this.fb.group({
		id: [null],
		gender: [null, [Validators.required]],
		firstName: [null,  [Validators.required, Validators.minLength(2)]],
		lastName: [null,  [Validators.required, Validators.minLength(2)]],
		email: [null, [Validators.required, Validators.pattern(ValidationPatterns.email)]],
		phoneNumber: [null, [Validators.required, Validators.pattern(ValidationPatterns.phoneNumber)]]
	});
	isInvalid = input.required<boolean>();
	isContactDisabled = model<boolean>();
	
	onChangeSub!: Subscription;
	onTouched = () => {
	};
	
	constructor() {
		effect(() => {
			if (this.isInvalid())
				invokeBlurOnInvalidFormControllers(this.form, this.el);
		});
	}
	
	validate(control: AbstractControl): ValidationErrors | null {
		if (this.form.valid) {
			return null;
		}
		return {
			invalidForm: {valid: false, message: "Eltern Felder sind ungültig!"},
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
		if (value) {
			const inputKeys = Object.keys(value);
			const formKeys = Object.keys(this.form.value);
			let obj: any = {};
			for (const key of formKeys) {
				if (inputKeys.includes(key)) {
					obj[key] = value[key];
				}
			}
			this.form.setValue(obj);
		}
	}
	
	onSliderChange() {
		this.isContactDisabled.update(e => !e);
		this.isContactDisabled() ?
			this.form.disable() :this.form.enable();
	}
	
	ngOnDestroy(): void {
		this.onChangeSub?.unsubscribe();
	}
}
