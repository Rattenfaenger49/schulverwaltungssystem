import {Component, effect, ElementRef, inject, input, OnDestroy} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor, FormBuilder,
  FormsModule,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule, ValidationErrors,
  Validator, Validators
} from "@angular/forms";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {Subscription} from "rxjs";
import {ibanValidator} from "../../../_services/iban-validator";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";

@Component({
  selector: 'ys-bank-daten',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, FormErrorDirective],
  templateUrl: './bank-daten.component.html',
  styleUrl: './bank-daten.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: BankDataComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: BankDataComponent,
    },
  ],

})
export class BankDataComponent implements ControlValueAccessor, Validator, OnDestroy {
  
  el = inject(ElementRef);
  fb = inject(FormBuilder);
  isInvalid = input.required<boolean>();
  
  form = this.fb.group({
    id: [null],
    accountHolderName: ["", Validators.required],
    bankName: ["", [Validators.required, Validators.minLength(3)]],
    iban: ["", [ibanValidator()]],
    bic: ["", [Validators.required, Validators.minLength(8)]],
  });
  onTouched = () => {};
  onChangeSub!: Subscription;
  
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
      invalidForm: { valid: false, message: "bank data fields invalid!" },
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
  
  ngOnDestroy(): void {
    this.onChangeSub?.unsubscribe();
  }
}
