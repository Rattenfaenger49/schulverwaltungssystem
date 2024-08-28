import {Component, Inject, OnInit, signal} from '@angular/core';
import {NgxExtendedPdfViewerModule} from "ngx-extended-pdf-viewer";
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'ys-pdf-viewer',
  standalone: true,
	imports: [
		NgxExtendedPdfViewerModule,
		MatButton,
		MatDialogActions,
		MatDialogTitle,
		MatDialogContent
	],
  templateUrl: './pdf-viewer.component.html',
  styleUrl: './pdf-viewer.component.scss'
})
export class PdfViewerComponent {
	
	constructor(@Inject(MAT_DIALOG_DATA) public data: { pdfSrc:any },
				private dialogRef: MatDialogRef<PdfViewerComponent>) {

	}
	
	onClose(): void {
		this.dialogRef.close(false);
	}
	createPdfBlobFromBase64(base64Data: string): { url: string; blob: Blob } {
		// Decode the base64 string to binary data
		const binaryString = window.atob(base64Data);
		const binaryLen = binaryString.length;
		const bytes = new Uint8Array(binaryLen);
		
		// Convert binary string to Uint8Array
		for (let i = 0; i < binaryLen; i++) {
			bytes[i] = binaryString.charCodeAt(i);
		}
		
		// Create a Blob from the Uint8Array
		const blob = new Blob([bytes], { type: 'application/pdf' });
		
		// Create a URL for the Blob
		const url = URL.createObjectURL(blob);
		
		return { url, blob };
	}
}
