import {Component, effect, ElementRef, inject, input, model, OnDestroy} from "@angular/core";
import {
	AbstractControl,
	ControlValueAccessor,
	FormBuilder,
	FormsModule,
	NG_VALIDATORS,
	NG_VALUE_ACCESSOR,
	ReactiveFormsModule,
	ValidationErrors,
	Validator,
	Validators,
} from "@angular/forms";
import {Subscription} from "rxjs";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import { ValidationPatterns} from "../../../validators/custom-validations";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {NgOptimizedImage} from "@angular/common";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
	selector: "ys-contact",
	templateUrl: "./contact.component.html",
	styleUrls: ["./contact.component.css"],
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			multi: true,
			useExisting: ContactComponent,
		},
		{
			provide: NG_VALIDATORS,
			multi: true,
			useExisting: ContactComponent,
		},
	],
	standalone: true,
	imports: [
		FormsModule,
		ReactiveFormsModule,
		FormErrorDirective,
		NgOptimizedImage,
		MatSlideToggle,
	],
})
export class ContactComponent
	implements ControlValueAccessor, Validator, OnDestroy {
	
	el = inject(ElementRef);
	fb = inject(FormBuilder);
	isContactDisabled = model<boolean>();
	
	required = input<boolean>(false);
	form = this.fb.group({
		id: [null],
		gender: ["", [Validators.required]],
		firstName: [null, [Validators.required, Validators.minLength(3)]],
		lastName: [null, [Validators.required]],
		phoneNumber: [null, [Validators.required,Validators.pattern(ValidationPatterns.phoneNumber)]],
		email: [null, [Validators.required,Validators.pattern(ValidationPatterns.email)]],
	});
	onTouched = () => {
	};
	onChangeSub!: Subscription;
	isInvalid = input.required<boolean>();
	
	constructor() {
		effect(() => {
			if (this.isInvalid()) {
				// this.form.markAllAsTouched();
				invokeBlurOnInvalidFormControllers(this.form, this.el);
				
			}
		});

	}
	
	validate(control: AbstractControl<any, any>): ValidationErrors | null {
		if (this.form.valid) {
			return null;
		}
		return {
			invalidForm: {valid: false, message: "Contact fields invalid!"},
		};
	}
	
	registerOnChange(fn: any): void {
		this.onChangeSub = this.form.valueChanges.subscribe(fn);
	}
	
	registerOnTouched(fn: any): void {
		this.onTouched = fn;
	}
	
	setDisabledState?(isDisabled: boolean): void {
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
		
	}

	ngOnDestroy(): void {
		this.onChangeSub?.unsubscribe();
	}
	

}
