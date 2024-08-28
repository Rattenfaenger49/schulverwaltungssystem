import {Component, effect, ElementRef, inject, signal} from '@angular/core';
import { AuthService } from '../../_services/data/auth.service';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from "@angular/forms";
import {regexValidator, ValidationPatterns} from "../../validators/custom-validations";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ResponseObject} from "../../types/ResponseObject";
import {DialogDataResponse, ResponseDialogComponent} from "../dialogs/response-dialog/response-dialog.component";
import { AddressComponent } from '../shared/address/address.component';
import {JsonPipe, NgOptimizedImage} from '@angular/common';
import { FormErrorDirective } from '../../_services/directivs/form-error-directive';
import {MatError} from "@angular/material/form-field";
import {MatAutocompleteOrigin} from "@angular/material/autocomplete";
import {ParentComponent} from "../parent/parent.component";
import {LoaderComponent} from "../shared/loader/loader.component";
import {invokeBlurOnInvalidFormControllers} from "../../_services/utils";
import {ToastrService} from "../../_services/ToastrService";

@Component({
  selector: "ys-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    FormErrorDirective,
    AddressComponent,
    JsonPipe,
    MatError,
    MatAutocompleteOrigin,
    ParentComponent,
    LoaderComponent,
    NgOptimizedImage,
  ],
})
export class RegisterComponent {
  form: FormGroup;
  isInvalid = signal<boolean>(false);
  isContactDisabled = signal(false);
  
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private router: Router,
  ) {
    this.form = this.fb.group({
      gender: [null, Validators.required],
      userType: [null, Validators.required],
      firstName: [null, [Validators.required, Validators.minLength(2)]],
      lastName: [null, [Validators.required, Validators.minLength(2)]],
      birthdate: [null, [Validators.required]],
      username: [
        null,
        [Validators.required, Validators.pattern(ValidationPatterns.email)],
      ],
      address: [null],
      parent: [null],
      phoneNumber: [
        null,
        [
          Validators.required,
          Validators.pattern(ValidationPatterns.phoneNumber),
        ],
      ],
      portalAccess: [false],
    });
    this.form.get("userType")?.valueChanges.subscribe((userType: string) => {
      if (userType === "TEACHER") {
        if (this.form.contains("level")) {
          this.form.removeControl("level");
          this.form.removeControl("contract");
        }
        this.form.addControl("education", this.fb.control(null));
        this.form.addControl("qualifications", this.fb.control(null));
        this.form.addControl("taxId", this.fb.control(null,[Validators.required, regexValidator(/^DE\d{10,11}$/)]));
        this.form.addControl("hourlyRate", this.fb.control(12, [ regexValidator(/^[0-9]+(\.[0-9]{2})?$/)]));
        this.form.addControl("hourlyRateGroup", this.fb.control(12, [ regexValidator(/^[0-9]+(\.[0-9]{2})?$/)]));

      } else if (userType === "STUDENT") {
        if (this.form.contains("education")) {
          this.form.removeControl("education");
          this.form.removeControl("qualifications");
          this.form.removeControl("hourlyRate");
          this.form.removeControl("hourlyRateGroup");
          this.form.removeControl("taxId");
        }
        this.form.addControl("level", this.fb.control(null, Validators.required));
      }
    });
    
    effect(()=>{
      this.isContactDisabled() ?
          this.form.get('parent')?.disable() :this.form.get('parent')?.enable();
    });
    
  }
  el = inject(ElementRef);
  toastr = inject(ToastrService);
  
  onSubmit() {
    
    if (this.form.invalid || this.form.pristine) {
      this.isInvalid.set(true);
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      this.toastr.warning("",  this.form.invalid
          ? "Bitte füllen Sie alle erforderlichen Felder aus"
          : "Es gibt keine Änderungen zum Speichern");

      return;
    }

    this.authService
      .register(this.form?.value)
      .subscribe({
        next: (res) => {
          this.openResponseDialog(res, true);
          this.router.navigateByUrl("/dashboard");
        }
      });
  }
  private openResponseDialog(response: ResponseObject<any>, success: boolean) {
    const data: DialogDataResponse = {
      response: response,
      success: success,
    };

    const dialogRef = this.dialog.open(ResponseDialogComponent, {
      width: "auto",
      maxWidth: "550px",
      data,
    });
    dialogRef.afterClosed().subscribe({
      next: () => {},
      error: (err: any) => console.error(err),
    });
  }
  
}
