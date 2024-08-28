import {Component, input} from '@angular/core';
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatFormField, MatInput, MatInputModule} from "@angular/material/input";

@Component({
  selector: "ys-input-date",
  standalone: true,
  imports: [
    FormErrorDirective,
    FormsModule,
    MatDatepicker,
    MatInput,
    MatFormField,
    MatDatepickerInput,
    MatDatepickerToggle,
    ReactiveFormsModule,
      MatInputModule,
  ],
  templateUrl: "./input-date.component.html",
  styleUrl: "./input-date.component.css",
})
export class InputDateComponent {
  formControl = input<FormControl<any>>(new FormControl());
  isSubmitted = input(false);
  isDateEnabled = input<any>(false);
  label = input<string>("");
}
