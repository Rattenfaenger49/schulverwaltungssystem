import {Component, ElementRef, EventEmitter, inject, OnInit, signal} from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import { Institution } from "../../../types/Institution";
import { ValidationPatterns } from "../../../validators/custom-validations";
import { ContactComponent } from "../../shared/contact/contact.component";
import { AddressComponent } from "../../shared/address/address.component";
import { FormErrorDirective } from "../../../_services/directivs/form-error-directive";
import { InstitutionService } from "../../../_services/data/institution.service";
import { finalize } from "rxjs";
import {JsonPipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {CustomDialogComponent, DialogData} from "../../dialogs/custom-dialog/custom-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {AuthService} from "../../../_services/data/auth.service";
import {ResponseService} from "../../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {Role} from "../../../types/enums/role";

@Component({
  selector: "ys-institution",
  templateUrl: "./institution.component.html",
  styleUrls: ["./institution.component.css"],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    FormErrorDirective,
    AddressComponent,
    ContactComponent,
    JsonPipe,
    MatIcon,
  ],
})
export class InstitutionComponent implements OnInit {
  institution?: Institution;
  form!: FormGroup;
  isInvalid = signal<boolean >(false);
  responseService = inject(ResponseService);
  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private router: Router,
    public auth: AuthService,
    private dialog: MatDialog,
    private instituionService: InstitutionService
  ) {
    this.institution = this.route.snapshot.data["institution"].data;
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      id: [this.institution?.id],
      name: [
        { value: this.institution?.name, disabled: false },
        [Validators.required, Validators.minLength(3)],
      ],
      phoneNumber: [
        this.institution?.phoneNumber,
        [
          Validators.required,
          Validators.pattern(ValidationPatterns.phoneNumber),
        ],
      ],
      email: [
        this.institution?.email,
        [Validators.required, Validators.pattern(ValidationPatterns.email)],
      ],
      comments: [""],
      address: [this.institution?.address, Validators.required],
      contacts: [this.institution?.contacts[0]],
    });
  }
  el = inject(ElementRef);
  onSubmit() {

    if (this.form?.invalid || this.form?.pristine) {
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      this.isInvalid.set(true);
      
      return;
    }
      const data = { ...this.form.value };
      data.contacts = [{ ...this.form.value.contacts }];

      this.instituionService
        .updateInstitution(data)
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

  delete() {

    this.openDialog().subscribe({
      next: (res: any) => {
        if (res) {
          this.instituionService.deleteInstitution(this.form.value.id).subscribe({
            next: (res) => {
              this.responseService.responseDialog(res, true);
              this.router.navigateByUrl("/institutions");
            },
            error: (err) => {
              console.error(err);
            },
          });
        }
      },
      error: (err: any) => console.error(err),
    });
  }
  openDialog(): EventEmitter<any> {
    /**
     * Sind Sie sicher, dass Sie diesen Schüler vom Lehrer entfernen möchten?
     * Bitte beachten Sie, dass diese Aktion die Beziehung zwischen dem Schüler und dem Lehrer trennen wird,
     * jedoch nicht den Datensatz des Schülers löschen wird.
     * */
    const data: DialogData = {
      type: "warning",
      message:
          "Sind Sie Sicher, dass Sie diese Geschäftsstelle löschen möchten?\n" +
          "Bitte beachten Sie, dass Diese Aktion nicht rückgängig gemacht werden kann! "
    };

    const dialogRef = this.dialog.open(CustomDialogComponent, {
      width: "auto",
      maxWidth: "550px",
      data,
    });
    return dialogRef.componentInstance.dialogResult;
  }
	
	
	protected readonly Role = Role;
}
