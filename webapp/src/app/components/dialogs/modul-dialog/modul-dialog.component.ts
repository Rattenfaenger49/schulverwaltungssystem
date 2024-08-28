import {Component, Inject, inject, OnInit, signal} from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
  ReactiveFormsModule,
  FormControl,
  ValidationErrors, AbstractControl, ValidatorFn
} from "@angular/forms";
import {
  MatDialogRef,
  MatDialogModule,
  MatDialogConfig, MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {JsonPipe, NgIf} from "@angular/common";
import {MatInput} from "@angular/material/input";
import {LessonDuration, LessonDurationKV} from "../../../types/enums/LessonDuration";
import {ModuleType} from "../../../types/enums/module-type";
import {MatCheckbox} from "@angular/material/checkbox";
import {halfOrWholeNumberValidator, numberInput} from "../../../validators/custom-validations";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {ToastrService} from "../../../_services/ToastrService";


@Component({
  selector: "ys-modul-dialog",
  templateUrl: "./modul-dialog.component.html",
  styleUrls: ["./modul-dialog.component.css"],
  standalone: true,
  imports: [
    MatDialogModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    FormErrorDirective,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    MatFormFieldModule,
    JsonPipe,
    MatInput,
    MatCheckbox,
    NgIf,
  ],
})
export class ModulDialogComponent implements OnInit {


  form!: FormGroup;
  isSubmitted = signal<boolean>(false);
  toastr = inject(ToastrService);
  constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      private fb: FormBuilder,
    private dialogRef: MatDialogRef<ModulDialogComponent>
  ) {
    this.form = this.fb.group({
      id: [data?.id ?? null],
      modulType: [data?.modulType ?? null, Validators.required],
      units: [data?.units ?? null,  [Validators.required, Validators.min(0.5),halfOrWholeNumberValidator()]],
      lessonDuration: [data?.lessonDuration ?? null, [Validators.required]],
      singleLessonAllowed: [data?.singleLessonAllowed ?? null],
      groupLessonAllowed: [data?.groupLessonAllowed ?? null],
      singleLessonCost: [data?.singleLessonCost ?? 0],
      groupLessonCost: [data?.groupLessonCost ?? 0]
    }, {validators: this.atLeastOneLessonAllowedValidator});
  }

  ngOnInit(): void {



    
    
    this.form.get('singleLessonAllowed')?.valueChanges.subscribe(value => {
      if (value) {
        this.form.get('singleLessonCost')?.setValidators([numberInput(), Validators.min(1),Validators.required]);
        
      } else {
        this.form.get('singleLessonCost')?.clearValidators();
      }
      this.form.get('singleLessonCost')?.updateValueAndValidity();
      
    });
    
    this.form.get('groupLessonAllowed')?.valueChanges.subscribe(value => {
      if (value) {
        this.form.get('groupLessonCost')?.setValidators([numberInput(), Validators.min(1),Validators.required]);
        
      } else {
        this.form.get('groupLessonCost')?.clearValidators();
      }
      this.form.get('groupLessonCost')?.updateValueAndValidity();
      
    });
    
  }
  
  atLeastOneLessonAllowedValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const isSingleLessonAllowed = control.get('singleLessonAllowed')?.value;
    const isGroupLessonAllowed = control.get('groupLessonAllowed')?.value;
    
    return isSingleLessonAllowed || isGroupLessonAllowed ? null : { atLeastOneLessonAllowed: 'Mindestens eine Unterrichtsoption muss erlaubt sein.' };
  };
  onClose(): void {
    this.dialogRef.close(false);
  }

  onSave(): void {
    this.isSubmitted.set(true);
    if (this.form.invalid ) {
      this.form.markAllAsTouched();
      // invokeBlurOnInvalidFormControllers(this.form, this.el);
      this.toastr.warning("","Bitte füllen Sie alle erforderlichen Felder aus");
      
      return;
    }

    this.dialogRef.close(this.form.value);
    
  }
	
	
	protected readonly LessonDurationKV = LessonDurationKV;
  protected readonly Object = Object;
  protected readonly ModuleType = ModuleType;
}
