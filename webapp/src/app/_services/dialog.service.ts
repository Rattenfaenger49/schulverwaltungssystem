import {EventEmitter, inject, Injectable} from '@angular/core';
import {ResponseObject} from "../types/ResponseObject";
import {MatDialog} from "@angular/material/dialog";
import {
	DialogDataResponse,
	ResponseDialogComponent
} from "../components/dialogs/response-dialog/response-dialog.component";
import {CustomDialogComponent, DialogData} from "../components/dialogs/custom-dialog/custom-dialog.component";
import {AttendeesDialogComponent} from "../components/dialogs/attendees-dialog/attendees-dialog.component";
import {AssignmetDialogComponent} from "../components/dialogs/assignmet-dialog/assignmet-dialog.component";
import {BankInfoDialogComponent} from "../components/dialogs/bank-info-dialog/bank-info-dialog.component";
import {Invoice} from "../types/Invoice";
import {PdfViewerComponent} from "../components/dialogs/pdf-viewer/pdf-viewer.component";

@Injectable({
	providedIn: 'root'
})
export class DialogService {
	
	dialog = inject(MatDialog);
	
	
	confirmationDialog(message: string, type = 'warning'): EventEmitter<any> {
		/**
		 * type: 'warning' | 'success' | 'ask' | 'hint'
		 * Sind Sie sicher, dass Sie diesen Schüler vom Lehrer entfernen möchten?
		 * Bitte beachten Sie, dass diese Aktion die Beziehung zwischen dem Schüler und dem Lehrer trennen wird,
		 * jedoch nicht den Datensatz des Schülers löschen wird.
		 * */
		const data: DialogData = {
			type: type,
			message: message,
			
		};
		
		const dialogRef = this.dialog.open(CustomDialogComponent, {
			width: "auto",
			maxWidth: "550px",
			data,
		});
		return dialogRef.componentInstance.dialogResult;
	}
	
	addAttendees(data: any) {
		
		this.dialog.open(AttendeesDialogComponent, {
			data,
			width: "550px",
			disableClose: true,
			autoFocus: false
		});
		
	}
	
	openAssignDialog() {
		return this.dialog.open(AssignmetDialogComponent, {
			height: '30vh',
			disableClose: true,
			autoFocus: false
		});
		
	}
	
	openBankDialog(userId: any) {
		const dialogRef = this.dialog.open(BankInfoDialogComponent,{
			disableClose: true,
			data: userId
		});
		
		dialogRef.afterClosed().subscribe(result => {
		});
	}
	
	
	viewPdf(pdf: any, invoice: Invoice) {
		return this.dialog.open(PdfViewerComponent, {
			data: { pdfSrc: pdf },
			width: '80%',
			height: '90%'
		});
	}
}
