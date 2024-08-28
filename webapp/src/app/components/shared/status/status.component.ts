import {Component} from '@angular/core';
import {Status} from "../../../types/enums/status";
import {
 ReactiveFormsModule
} from "@angular/forms";

import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";

@Component({
  selector: "ys-status",
  standalone: true,
  imports: [ FormErrorDirective, ReactiveFormsModule],
  templateUrl: "./status.component.html",
  styleUrl: "./status.component.css",

})
export class StatusComponent
{

}
