import { Component } from '@angular/core';
import {MatFormField} from "@angular/material/form-field";
import {MatDialogActions, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {NgTemplateOutlet} from "@angular/common";

@Component({
  selector: 'ys-filter',
  standalone: true,
	imports: [
		MatFormField,
		MatDialogContent,
		MatDialogTitle,
		FormsModule,
		MatButton,
		MatDialogActions,
		MatInput,
		NgTemplateOutlet
	],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.scss'
})
export class FilterComponent {
	
	startDate: any;
	endDate: any;
	name: any;
}
