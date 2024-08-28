import {Component, EventEmitter, inject, Inject, Input, Output, signal} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {ResponseObject} from "../../../types/ResponseObject";
import {zoomAnimation} from "../../../animations/zoomAnimation";
import {MatButtonModule} from '@angular/material/button';


export interface DialogDataResponse {
	success: boolean;
	response: ResponseObject<any>;
}

@Component({
	selector: 'ys-response-dialog',
	templateUrl: './response-dialog.component.html',
	styleUrls: ['./response-dialog.component.scss'],
	animations: [zoomAnimation],
	standalone: true,
	imports: [MatDialogModule, MatButtonModule]
})
export class ResponseDialogComponent {
	
	dialogRef = inject(MatDialogRef<ResponseDialogComponent>)
	data = signal<DialogDataResponse >({
		success: false,
		response: { message: 'Etwas ist schief gelaufen', data: null, status: '' }
	});
	@Output() dialogResult: EventEmitter<boolean> = new EventEmitter<boolean>();
	
	constructor(
		@Inject(MAT_DIALOG_DATA) public dialogData: DialogDataResponse
	) {
		this.data.set(dialogData);
		
	}
	
	close(): void {
		
		this.dialogRef.close();
	}
	
	protected readonly zoomAnimation = zoomAnimation;
}
