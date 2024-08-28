import {Component, EventEmitter, inject, input, Output} from '@angular/core';
import {InvoiceStatus} from "../../../types/enums/InvoiceStatus";
import {NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {Invoice} from "../../../types/Invoice";
import {InvoiceService} from "../../../_services/data/invoice.service";
import {ResponseService} from "../../../_services/ResponseService";
import {SignatureDialogComponent} from "../../dialogs/signature-dialog/signature-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
	selector: 'ys-invoice-step',
	standalone: true,
	imports: [
		NgSwitch,
		MatTooltip,
		MatIcon,
		NgSwitchDefault,
		NgSwitchCase
	],
	templateUrl: './invoice-step.component.html',
	styleUrl: './invoice-step.component.scss'
})
export class InvoiceStepComponent {
	
	
	invoice = input.required<Invoice>();
	@Output() stepChanged = new EventEmitter<Invoice>();
	
	invoiceService = inject(InvoiceService)
	responseService = inject(ResponseService)
	dialog = inject(MatDialog)
	
	
	protected readonly InvoiceStatus = InvoiceStatus;
	protected readonly onclick = onclick;
	
	onChangeStep(status: InvoiceStatus) {
		if (status == InvoiceStatus.UNDER_REVIEW) {
			// if signed, status will change automaticly
			this.openSignatureDialogAndSignInvoice();
			return;
			
		}
		const updatedInvoice = {...this.invoice(), invoiceStatus: status};
		
		this.invoiceService.updateStatus(updatedInvoice).subscribe({
			next: res => {
				this.responseService.responseDialog(res, true);
				this.invoice().invoiceStatus = status;
				this.stepChanged.emit(res.data);
			}
		});
		
	}
	
	openSignatureDialogAndSignInvoice(): void {
		const dialogRef = this.dialog.open(SignatureDialogComponent);
		
		dialogRef.afterClosed().subscribe(
			{
				next: (res) => {
					if (res) {
						// Handle the signature data here
						this.invoiceService.addSignature(this.invoice().id, res).subscribe(
							{
								next: () => {
									this.invoice().invoiceStatus = InvoiceStatus.UNDER_REVIEW;
								}
							}
						); // Wait for signature addition
					}
				},
				error: err => console.error(err)
			}
		);
		
	}
	
}
