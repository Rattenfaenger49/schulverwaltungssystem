import {Component, effect, ElementRef, inject, Input, input, OnDestroy} from '@angular/core';
import { AbstractControl, ControlValueAccessor, FormBuilder, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator, Validators, FormsModule, ReactiveFormsModule } from "@angular/forms";
import {Subscription} from "rxjs";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {exactLength,  digitsInput} from "../../../validators/custom-validations";

@Component({
  selector: "ys-address",
  templateUrl: "./address.component.html",
  styleUrls: ["./address.component.scss"],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: AddressComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: AddressComponent,
    },
  ],
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, FormErrorDirective],
})
export class AddressComponent
  implements ControlValueAccessor, Validator, OnDestroy
{
  el = inject(ElementRef);
  fb = inject(FormBuilder);
  
  required = input<boolean>(true) ;
  form = this.fb.group({
    id: [null],
    street: ["", [Validators.minLength(3), Validators.required]],
    streetNumber: ["", Validators.required],
    city: ["", [Validators.minLength(2), Validators.required]],
    country: ["", [Validators.minLength(2), Validators.required]],
    state: ["", [Validators.minLength(2), Validators.required]],
    postal: ["",[Validators.required, digitsInput(), exactLength(5)]],
  });
  onTouched = () => {};
  onChangeSub!: Subscription;
  isInvalid = input.required<boolean >();
  schowTitle = input<boolean>(true);
  constructor() {
    effect( ()=> {
      if(this.isInvalid())
        // this.form.markAllAsTouched();
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
    if (value) {
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
