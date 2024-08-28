import {Component, ElementRef, inject, signal} from '@angular/core';
import {AsyncPipe, NgClass} from "@angular/common";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {ValidationPatterns} from "../../../validators/custom-validations";
import {AuthService} from "../../../_services/data/auth.service";
import {finalize} from "rxjs";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: "ys-reset-password",
  standalone: true,
	imports: [AsyncPipe, FormErrorDirective, FormsModule, RouterLink, NgClass, ReactiveFormsModule, MatIcon],
  templateUrl: "./reset-password.component.html",
  styleUrl: "./reset-password.component.scss",
})
export class ResetPasswordComponent {
  form = this.fb.group({
    username: ['', [Validators.required, Validators.pattern(ValidationPatterns.email)]],
    schoolId: ['', [Validators.required]],
  });
  success = false;
  requestSend = false;
  isSubmitted = signal<boolean >(false);
  el = inject(ElementRef);
  auth = inject(AuthService);
  constructor(private fb: FormBuilder) {
    const schoolId = this.auth.getSchoolId();
    
    if (!schoolId) {
    }else{
      this.form.get('schoolId')?.setValue(schoolId);
      this.form.get('schoolId')?.disable();
    }
  }
  onSubmit() {
    this.isSubmitted.set(true);
    if(this.form.invalid || this.form.pristine || !this.form.getRawValue().schoolId) {
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      return;
    }
    
    this.auth.saveSchoolId(this.form.getRawValue().schoolId as string);
    this.auth
      .resetPassword(this.form.value)
      .pipe(
        finalize(() => {
          this.requestSend = true;
          this.form.markAsPristine();
        }),
      )
      .subscribe({
        next: (res) => {
          if (res.status !== "SUCCESSFUL") this.success = true;
        },
        error: (err) => {
          this.success = false;
          console.error(err);
        },
      });
  }
  
  changeSchoolId() {
    this.form.get('schoolId')?.enable();
  }
}
