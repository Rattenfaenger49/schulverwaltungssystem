import {Component, ElementRef, inject, OnInit, signal} from "@angular/core";
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { FormErrorDirective } from "../../../_services/directivs/form-error-directive";
import { AddressComponent } from "../../shared/address/address.component";
import { ContactComponent } from "../../shared/contact/contact.component";
import { JsonPipe } from "@angular/common";
import { Institution } from "../../../types/Institution";
import { InstitutionService } from "../../../_services/data/institution.service";
import { ValidationPatterns } from "../../../validators/custom-validations";
import { finalize } from "rxjs";
import {ResponseService} from "../../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";

@Component({
  selector: "ys-create-institution",
  templateUrl: "./create-institution.component.html",
  styleUrls: ["./create-institution.component.css"],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    FormErrorDirective,
    AddressComponent,
    ContactComponent,
    JsonPipe,
  ],
})
export class CreateInstitutionComponent implements OnInit {
  institution?: Institution;
  form!: FormGroup;
  isInvalid = signal<boolean>(false);
  responseService = inject(ResponseService);
  
  constructor(
    private fb: FormBuilder,
    private instituionService: InstitutionService,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ["", [Validators.required, Validators.minLength(3)]],
      phoneNumber: [
        "",
        [
          Validators.required,
          Validators.pattern(ValidationPatterns.phoneNumber),
        ],
      ],
      email: [
        "",
        [Validators.required, Validators.pattern(ValidationPatterns.email)],
      ],
      comments: [""],
      address: ["", Validators.required],
      contacts: [null],
    });
  }

  get contacts() {
    return this.form.get("contacts") as FormArray;
  }
  el = inject(ElementRef);
  onSubmit() {

    if (this.form?.invalid || this.form?.pristine) {
      this.isInvalid.set(true);
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      return;
    }
    const data = { ...this.form.value };
    data.contacts = [{ ...this.form.value.contacts }];

    this.instituionService
      .createInstitution(data)
      .pipe(
        finalize(() => {
          this.form.markAsPristine();
        }),
      )
      .subscribe({
        next: (res) => {
          this.responseService.responseDialog(res, true);
        },
        error: (err) => {
          console.error(err);
        },
      });
  }
}
