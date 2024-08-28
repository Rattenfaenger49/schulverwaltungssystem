import {Component, ElementRef, inject, signal} from "@angular/core";
import { AuthService } from "../../../_services/data/auth.service";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";

import { FormErrorDirective } from "../../../_services/directivs/form-error-directive";
import { DialogService } from "../../../_services/dialog.service";
import { finalize } from "rxjs";
import { MatIcon } from "@angular/material/icon";
import { JsonPipe } from "@angular/common";
import {ResponseService} from "../../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";

@Component({
  selector: "ys-update-password",
  templateUrl: "./update-password.component.html",
  styleUrls: ["./update-password.component.css"],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    FormErrorDirective,
    MatIcon,
    JsonPipe,
  ],
})
export class UpdatePasswordComponent {
  form!: FormGroup;
  confirmationToken: string | null;
  msg = "";
  isPasswordSet = false;
  passowrdType = "password";
  passowrdTypeConfirm = "password";

  isSubmitted = signal<boolean>(false);
  
  responseService = inject(ResponseService);
  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private diaService: DialogService,
    private route: ActivatedRoute,
  ) {
    this.confirmationToken = this.route.snapshot.queryParamMap.get("token");

    this.form = this.fb.group({
      password: ["", [Validators.minLength(8), this.passwordValidationFn]],
      confirmationPassword: [
        "",
        [Validators.required,],
      ],
    },
        {validators :   this.passwordConfirmationFn} );
    this.form.get("password")?.valueChanges.subscribe(() => {
      this.form.get("confirmPassword")?.updateValueAndValidity();
    });
  }

  passwordValidationFn(
    control: FormControl,
  ): { [key: string]: boolean } | null {
    const password = control.value;
    const errors: any = {};

    if (!/\d/.test(password)) {
      errors.hasNumber = true;
    }
    if (!/[A-Z]/.test(password)) {
      errors.hasUpper = true;
    }
    if (!/[a-z]/.test(password)) {
      errors.hasLower = true;
    }
    if (!/[^a-zA-Z\d]+/.test(password)) {
      errors.hasSpecialCharacter = true;
    }

    return Object.keys(errors).length !== 0 ? errors : null;
  }

  passwordConfirmationFn(
      control: FormControl,
  ): { [key: string]: boolean } | null {

    const passwordValid = control.value.confirmationPassword === control.value.password;


    if (!passwordValid) {
      return { passwordMismatch: true };
    }

    return null;
  }
  el = inject(ElementRef);

  onSubmit() {
    this.isSubmitted.set(true);
    if (this.form.invalid || this.form.pristine) {
      
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      return;
    }
    const data = { ...this.form.value };
    data.confirmationToken = this.confirmationToken;
    this.authService
      .setPassword(data)
      .pipe(
        finalize(() => {
          this.form.markAsPristine();
        }),
      )
      .subscribe({
        next: (res) => {
          this.isPasswordSet = true;
          this.msg = res.message;
          this.responseService.responseDialog(res, true, () => {
            setTimeout(() => this.router.navigateByUrl("/login"), 1500);
          });
        },
        error: (err) => {
          console.error(err);
        },
      });
  }
  togglePasswordVisibility() {
    this.passowrdType = this.passowrdType === "password" ? "text" : "password";
  }
  togglePasswordVisibilityConfirmation() {
    this.passowrdTypeConfirm =
      this.passowrdTypeConfirm === "password" ? "text" : "password";
  }
}
