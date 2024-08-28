import {Component, ElementRef, inject, OnInit, signal} from '@angular/core';
import {AddressComponent} from "../shared/address/address.component";
import {FormErrorDirective} from "../../_services/directivs/form-error-directive";
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {StatusStyleDirective} from "../../_services/directivs/status-style-directive";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {UserService} from "../../_services/data/user.service";
import {ValidationPatterns} from "../../validators/custom-validations";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import {DialogService} from "../../_services/dialog.service";
import {AuthService} from "../../_services/data/auth.service";
import {MatSelect} from "@angular/material/select";
import {LoaderComponent} from "../shared/loader/loader.component";
import {Admin} from "../../types/admin";
import {Student} from "../../types/student";
import { Teacher } from "src/app/types/teacher";
import {MatInput} from "@angular/material/input";
import {StudentComponent} from "../students/student/student.component";
import {TeacherComponent} from "../teachers/teacher/teacher.component";
import {ResponseService} from "../../_services/ResponseService";
import {invokeBlurOnInvalidFormControllers} from "../../_services/utils";
import {ToastrService} from "../../_services/ToastrService";
import {Gender} from "../../types/enums/gender";
import {Role} from "../../types/enums/role";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";


@Component({
  selector: "ys-profile",
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.css"],
  standalone: true,
	imports: [
		AddressComponent,
		FormErrorDirective,
		ReactiveFormsModule,
		StatusStyleDirective,
		RouterLink,
		NgIf,
		MatIcon,
		MatIconButton,
		MatTooltip,
		NgOptimizedImage,
		MatSelect,
		LoaderComponent,
		MatInput,
		StudentComponent,
		TeacherComponent,
		MatButton,
		MatChip,
		MatChipSet,
		CdkTextareaAutosize,
	],
})
export class ProfileComponent implements OnInit {
  form!: FormGroup;
  protected user!: Admin | Teacher |Student | any;
  userType!: string;
  isInvalid = signal<boolean>(false);
  
  responseService = inject(ResponseService);
  constructor(
      private fb: FormBuilder,
      private userService: UserService,
      protected auth: AuthService,
      private router: Router,
      private route: ActivatedRoute,
      private diaService: DialogService,
  ) {
    this.user = this.route.snapshot.data["user"].data;
    this.initializeProfileForm();
  }

  ngOnInit(): void {


  }
  el = inject(ElementRef);
  toastr = inject(ToastrService);

  onSubmit() {
   
    if (this.form.invalid || this.form.pristine) {
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      this.isInvalid.set(true);
      this.toastr.warning("", this.form.invalid
          ? "Bitte füllen Sie alle erforderlichen Felder aus"
          : "Es gibt keine Änderungen zum Speichern",)
      return;
    }

    this.userService.updateUserProfile(this.form.value)
        .subscribe({
      next: (res) => {
        this.form.markAsPristine();
        this.responseService.responseDialog(res, true);
      }
    });
  }

  private initializeProfileForm() {
    this.form = this.fb.group({
      id: [this.user.id, [Validators.required]],
      gender: [this.user.gender, [Validators.required]],
      firstName: [
        this.user.firstName,
        [Validators.required, Validators.minLength(2)],
      ],
      lastName: [
        this.user.lastName,
        [Validators.required, Validators.minLength(2)],
      ],
      birthdate: [this.user?.birthdate, [Validators.required]],
      username: [
        this.user.username,
        [Validators.required, Validators.pattern(ValidationPatterns.email)],
      ],
      phoneNumber: [
        this.user.phoneNumber,
        [
          Validators.required,
          Validators.pattern(ValidationPatterns.phoneNumber),
        ],
      ],
      comment: [this.user?.comment],
      address: [this.user.address, [Validators.required]],
    });
    this.userType = "admin";

    if ("qualifications" in this.user) {
      this.userType = "teacher";
      // User is a teacher
      this.form.addControl(
        "qualifications",
        new FormControl(this.user?.qualifications),
      );
      this.form.addControl(
        "education",
        new FormControl(this.user?.education),
      );
    } else if ("level" in this.user) {
      // User is a student
      this.userType = "student";
      this.form.addControl(
        "level",
        new FormControl(this.user.level, Validators.required),
      );
      this.form.addControl(
        "contracts",
        new FormControl(this.user.contracts),
      );
    }
  }
  delete() {
    const msg =
      "Sind Sie Sicher, dass Sie Ihr Account löschen möchten?\n" +
      "Bitte beachten Sie, dass Diese Aktion nicht rückgängig gemacht werden kann! ";
    this.diaService.confirmationDialog(msg)
        .subscribe(
      (res: any) => {
        if (res) {
          this.userService.deleteAccount()
              .subscribe({
            next: (res) => {
              this.responseService.responseDialog(res, true);
              this.auth.clearUserState();
              this.router.navigateByUrl("/login");
            },
            error: (err) => {
              console.error(err);
            },
          });
        }
      },
      (err: any) => console.error(err),
    );
  }
	
	protected readonly Gender = Gender;
	protected readonly Role = Role;
  
  openBankDialog() {
    this.diaService.openBankDialog(this.form.value!.id);
  }
}
