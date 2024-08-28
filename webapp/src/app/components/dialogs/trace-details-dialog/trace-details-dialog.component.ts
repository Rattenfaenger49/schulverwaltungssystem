import {Component, Inject, signal} from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {DatePipe} from "@angular/common";
import {Trace} from "../../../types/Trace";
@Component({
	selector: 'ys-trace-details-dialog',
	templateUrl: './trace-details-dialog.component.html',
	standalone: true,
	imports: [
		DatePipe,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogTitle
	],
	styleUrls: ['./trace-details-dialog.component.scss']
})
export class TraceDetailsDialogComponent {
	trace = signal<Trace|null>(null)
	constructor(
		public dialogRef: MatDialogRef<TraceDetailsDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: any
	) {
		this.trace.set(data.trace);
	}
	
	closeDialog() {
		this.dialogRef.close();
	}
}
