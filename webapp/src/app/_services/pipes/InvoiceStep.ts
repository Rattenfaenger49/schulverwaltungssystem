import {InvoiceStatus} from "../../types/enums/InvoiceStatus";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {inject, Pipe, PipeTransform} from "@angular/core";

@Pipe({
    standalone: true,
    pure: true,
    name: 'invoiceStep'
})
export class InvoiceStepPipe implements PipeTransform {
    sanitizer = inject( DomSanitizer);
    transform(value: InvoiceStatus): SafeHtml  {
        return this.sanitizer.bypassSecurityTrustHtml(this.getIconsHtml(value));
    }
    private getIconsHtml(status: InvoiceStatus): any {
 
        switch (status) {
            case InvoiceStatus.PENDING_APPROVAL:
                return  `<mat-icon tooltip="Freigeben">check</mat-icon>`;
            case InvoiceStatus.UNDER_REVIEW:
                return  `<mat-icon tooltip="akzeptiere">check</mat-icon><mat-icon tooltip="Ablehnen">cancel</mat-icon>`;
            case InvoiceStatus.ACCEPTED:
                return  `<mat-icon tooltip="haben Sie die rechnung bezahlt?">payment</mat-icon>`;
            case InvoiceStatus.REJECTED:
                return  `<mat-icon tooltip="Rechnung wurde abgelehnt">cancel</mat-icon>`;
            case InvoiceStatus.PAID:
                return  `<mat-icon tooltip="Rechnung ist bezahlt :-)">paid</mat-icon>`;
            default:
                return ''
        }
        
    }
}
