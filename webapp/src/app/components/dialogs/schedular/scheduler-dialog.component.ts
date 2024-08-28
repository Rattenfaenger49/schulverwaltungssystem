import {Component, ElementRef, inject, Inject, OnInit, signal} from "@angular/core";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators,} from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogConfig,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from "@angular/material/dialog";
import {MatFormField, MatFormFieldModule, MatLabel, MatSuffix,} from "@angular/material/form-field";
import {SchedularComponent} from "../../shared/schedular/schedular.component";
import {MatInput, MatInputModule} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {MatAutocompleteModule, MatOption,} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle,
} from "@angular/material/datepicker";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {AsyncPipe, DatePipe, JsonPipe, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {InputDateComponent} from "../../shared/input-date/input-date.component";
import {MatTooltip} from "@angular/material/tooltip";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {MatDatetimepickerModule} from "@mat-datetimepicker/core";
import {TimepickerModule} from "ngx-bootstrap/timepicker";

import {NgxMatDatetimePickerModule} from "@angular-material-components/datetime-picker";
import {ContractType} from "../../../types/enums/ContractType";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";

@Component({
  selector: "ys-schedular-dialog",
  standalone: true,
  providers: [],
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    FormErrorDirective,
    MatLabel,
    MatOption,
    MatSelect,
    MatFormFieldModule,
    MatInputModule,
    FormErrorDirective,
    MatDatepickerToggle,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelect,
    MatIcon,
    DatePipe,
    TimepickerModule,
    InputDateComponent,
    MatIconButton,
    MatTooltip,
    TooltipModule,
    MatAutocompleteModule,
    MatOptionModule,
    AsyncPipe,
    MatSuffix,
    DatePipe,
    MatDatetimepickerModule,
    FormsModule,
    NgIf,
    JsonPipe,
    NgxMatDatetimePickerModule,
  ],
  templateUrl: "./scheduler-dialog.component.html",
  styleUrl: "./scheduler-dialog.component.scss",
})
export class SchedulerDialogComponent implements OnInit {
  defaultDialogConfig: MatDialogConfig = {
    width: "auto",
    maxWidth: "550px",
    disableClose: true,
    autoFocus: true,
  };

title: string = "Add Termin";
  form!: FormGroup;
  isSubmitted = signal<boolean>(false);
  el = inject(ElementRef);
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<SchedularComponent>);
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }

  ngOnInit(): void {
    // Merge default configuration with provided data (if any)
    this.dialogRef.updateSize(this.defaultDialogConfig.width);
    if(this.data?.endAt == this.data?.startAt){
      const date = new Date(this.data?.endAt);
       date.setHours(date.getHours() + 1);
      this.data.endAt = date;
    }

    
    this.form = this.fb.group(
      {
        id: [this.data?.id],
        title: [this.data?.title, [Validators.required]],
        content: [this.data?.content],
        startAt: [{value: this.data?.startAt, disabled: true}, [Validators.required, this.timeComparisonValidator]],
        endAt: [{value: this.data?.endAt, disabled: true}, [Validators.required]],
        attendees: [this.data?.attendees],
        organizer: [this.data?.organizer],
        // participants: [this.scheduler?.participants],
        contractType: [
          this.data?.contractType ?? ContractType.WEEK,
          [Validators.required],
        ],
        status: [this.data?.status ?? "ok"],
      },
      { validators: this.timeComparisonValidator }
    );

  }

  // Custom validator function to check if startTime is before endTime
  timeComparisonValidator(form: FormGroup): { [p: string]: any } | null {
    if(!form)
      return null;

    const startAt = form.get("startAt");
    const endAt = form.get("endAt");
    if (!startAt?.getRawValue()|| !endAt?.getRawValue() || startAt.invalid || endAt.invalid) {
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      
      return null;
    }
    const startDate = new Date(startAt.getRawValue());
    const endDate = new Date(endAt.getRawValue());
    if (startDate.getTime() >= endDate.getTime()) {
      return { timeOrderError: true };
    }

    return null;
  }
  save() {
    if(this.form.invalid) {
      invokeBlurOnInvalidFormControllers(this.form, this.el);
      this.isSubmitted.set(true);
      return;
    }
    this.dialogRef.close({
      id: this.form.value.id,
      title: this.form.value.title,
      content: this.form.value.content,
      startAt: this.form.getRawValue().startAt,
      endAt: this.form.getRawValue().endAt,
      organizer: this.form.value.organizer,
      attendees: this.form.value.attendees,
      contractType: this.form.value.contractType,
      status: this.form.value.status,
    });
  }



  close() {
    this.dialogRef.close(null);
  }
  onInputChange(controller: string, newValue: string) {
    // Ensure input does not exceed 5 characters
    if (newValue.length > 5) {
      newValue = newValue.slice(0, 5);
    }

    // Remove non-numeric characters
    newValue = newValue.replace(/\D/g, "");

    // Ensure HH:mm format
    if (newValue.length > 2) {
      newValue = newValue.slice(0, 2) + ":" + newValue.slice(2);
    }

    // Ensure HH is between 00 and 23
    const hours = parseInt(newValue.substring(0, 2), 10);
    if (hours > 23) {
      newValue = "23" + newValue.slice(2);
    }

    // Ensure mm is between 00 and 59
    const minutes = parseInt(newValue.substring(3), 10);
    if (minutes > 59) {
      newValue = newValue.slice(0, 3) + "59";
    }

    // Update input value
    this.form.get(controller)?.setValue(newValue, { emitEvent: false });
  }

  formatDateTime(date: Date): string {
    // Format date to display only date and time without seconds
    return date.toISOString().slice(0, 16).replace('T', ' ');
  }
  
  protected readonly Object = Object;
  protected readonly ContractType = ContractType;
}
