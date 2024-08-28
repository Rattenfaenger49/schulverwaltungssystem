import {Component} from "@angular/core";


import { DProgressBar } from "../../_services/directivs/DProgressBar";
import { CdkOverlayOrigin } from "@angular/cdk/overlay";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from "@angular/forms";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatNativeDateModule } from "@angular/material/core";
import { MatCardModule } from "@angular/material/card";
import { DecimalPipe } from "@angular/common";

@Component({
  selector: "ys-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
  standalone: true,
  imports: [
    DProgressBar,
    CdkOverlayOrigin,
    FormsModule,
    MatInputModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatNativeDateModule,
    MatCardModule,
    DecimalPipe,
  ],
})
export class HomeComponent {

}
