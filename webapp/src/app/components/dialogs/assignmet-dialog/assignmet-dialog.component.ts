import {Component, OnInit} from '@angular/core';
import {StudentService} from "../../../_services/data/student.service";
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MatDialogModule } from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';
import { AutocompleteListComponent } from '../../shared/autocomplete-list/autocomplete-list.component';
import {AsyncPipe} from "@angular/common";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatFormField, MatHint, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
  selector: "ys-assignmet-dialog",
  templateUrl: "./assignmet-dialog.component.html",
  styleUrls: ["./assignmet-dialog.component.css"],
  standalone: true,
  imports: [
    MatDialogModule,
    FormsModule,
    ReactiveFormsModule,
    AutocompleteListComponent,
    MatButtonModule,
    AsyncPipe,
    MatAutocomplete,
    MatAutocompleteTrigger,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatOption,
  ],
})
export class AssignmetDialogComponent implements OnInit {
  form: FormGroup;
  data: any;
  students!: any[];

  constructor(
    private studentService: StudentService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AssignmetDialogComponent>,
  ) {
    this.form = this.fb.group({
      id: [null, Validators.required],
      firstName: ["", Validators.required],
      lastName: ["", Validators.required],
    });
  }
  selectedStudent(student: any) {
    this.form.patchValue({
      id: student.id,
      firstName: student.firstName,
      lastName: student.lastName,
    });
  }
  ngOnInit(): void {
    this.studentService.getStudentsFullname().subscribe({
      next: (res) => {
        this.students = res.data;
      },
    });
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

  onAssign(): void {
    if (this.form.valid) {
      const res = this.form.value;

      this.dialogRef.close(res);
    }
  }
}
