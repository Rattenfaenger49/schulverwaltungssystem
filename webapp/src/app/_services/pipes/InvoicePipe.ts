import {inject, Pipe, PipeTransform} from '@angular/core';
import {InvoiceStatus} from "../../types/enums/InvoiceStatus";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";

@Pipe({
    standalone: true,
    pure: true,
    name: 'invoiceStatus'
})
export class InvoicePipe implements PipeTransform {
    sanitizer = inject( DomSanitizer);
    transform(value: InvoiceStatus): SafeHtml  {
        const statusStyles = this.getStatusStyles(value);
        return this.sanitizer.bypassSecurityTrustHtml(`<span style="background-color:${statusStyles.backgroundColor};color:${statusStyles.color};padding:5px;border-radius:3px;">${value}</span>`);
    }
    private getStatusStyles(status: InvoiceStatus): { backgroundColor: string, color: string } {
        switch (status) {
            case InvoiceStatus.PENDING_APPROVAL:
                return { backgroundColor: 'orange', color: 'black' };
            case InvoiceStatus.UNDER_REVIEW:
                return { backgroundColor: 'yellow', color: 'black' };
            case InvoiceStatus.ACCEPTED:
                return { backgroundColor: 'green', color: 'white' };
            case InvoiceStatus.REJECTED:
                return { backgroundColor: 'red', color: 'white' };
            case InvoiceStatus.PAID:
                return { backgroundColor: 'blue', color: 'white' };
            default:
                return { backgroundColor: 'grey', color: 'white' };
        }
    }
}
