import {InvoiceStatus} from "../../types/enums/InvoiceStatus";
import {
	ComponentFactoryResolver,
	Directive,
	effect,
	ElementRef, Inject,
	inject,
	Input,
	input, RendererFactory2,
	ViewContainerRef
} from "@angular/core";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";


@Directive({
	selector: "[invoiceStep]",
	standalone: true,
})
export class InvoiceStepDirective {
	
	@Input({required: true}) invoiceStep!: InvoiceStatus;
	el = inject(ElementRef);
	
	constructor(private vcr: ViewContainerRef) {

	}
	
	
	private renderIcons(): void {
		const container = this.el.nativeElement;
		const icons = this.getIconsHtml();
		icons.forEach(icon => {
			const componentRef = this.vcr.createComponent(MatIcon);
			componentRef.instance.fontIcon = icon.name;

		});
	}
	
	private getIconsHtml(): { name: string, tooltip: string }[] {

		switch (this.invoiceStep) {
			case  InvoiceStatus.PENDING_APPROVAL:
				return [{name: 'check', tooltip: 'Freigeben'}];
			case InvoiceStatus.UNDER_REVIEW:
				return [
				{ name: 'check', tooltip: 'Akzeptiere' },
				{ name: 'cancel', tooltip: 'Ablehnen' }
			];
			case InvoiceStatus.ACCEPTED:
				return [{ name: 'payment', tooltip: 'Haben Sie die Rechnung bezahlt?' }];
			case InvoiceStatus.REJECTED:
				return [{ name: 'cancel', tooltip: 'Rechnung wurde abgelehnt' }];
			case InvoiceStatus.PAID:
				return [{ name: 'paid', tooltip: 'Rechnung ist bezahlt :-)' }];
			default:
				return [];
		}
		
	}
}
