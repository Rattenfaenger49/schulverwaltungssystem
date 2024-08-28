import {Directive, ElementRef, HostListener, inject, input, ViewContainerRef} from "@angular/core";
import {NgControl} from "@angular/forms";
// TODO
/*
*
  @Output() untouchedToTouched = new EventEmitter<void>();
  @Output() pristineToDirty = new EventEmitter<void>();

  constructor(private el: ElementRef) {
  *    this.statusChangesSubscription = this.ngControl.statusChanges.subscribe(() => {
      this.checkAndHandleTouched();
    });}

  @HostListener('blur', ['$event.target'])
  onBlur(target: HTMLInputElement) {
    if (target) {
      if (target.classList.contains('ng-untouched')) {
        this.untouchedToTouched.emit();
      }
      if (target.classList.contains('ng-pristine')) {
        this.pristineToDirty.emit();
      }
    }
  }
  * <input type="text" formControlName="myControl" formControlStatusChange (untouchedToTouched)="handleUntouchedToTouched()" (pristineToDirty)="handlePristineToDirty()">

*
* */
@Directive({
	selector: "[FormError]",
	standalone: true,
})
export class FormErrorDirective {
	customMessage = input("");
	vcr = inject(ViewContainerRef);
	el = inject(ElementRef);
	control = inject(NgControl);
	
	
	@HostListener("blur") onBlur() {
		if(this.customMessage() === 'IBAN nicht gültig'){
		
		}
		this.handleValidation();
	}
	
	private handleValidation() {
		
		const inValid = this.control.invalid;
		const element = this.el.nativeElement;
		
		if (inValid) {
			element.classList.add("is-invalid");
			const errorMessages = this.getErrorMessages();
			this.errorMessage(element, errorMessages);
		} else {
			element.classList.remove("is-invalid");
			this.removeErrorMessage(element);
		}
	}
	
	private getErrorMessages(): string {
		// TODO optimize this method
		const errors = this.control.errors;
		if (this.customMessage()) return this.customMessage();
		if (errors) {
			// Add more error cases as needed
			
			const messages: { [key: string]: string } = {
				minlength: `Mindestens ${errors['minlength']?.requiredLength} Zeichen erforderlich.`,
				min: `Der Wert muss >= ${errors['min']?.min} sein.`,
				max: `Der Wert muss <= ${errors['max']?.max} sein.`,
				pattern: 'Bitte geben Sie einen gültigen Wert ein.',
				email: 'Bitte geben Sie eine gültige E-Mail-Adresse ein.',
				hasUpper: 'Mindestens ein Großbuchstabe ist erforderlich.',
				hasLower: 'Mindestens ein Kleinbuchstabe ist erforderlich.',
				hasNumber: 'Mindestens eine Zahl ist erforderlich.',
				hasSpecialCharacter: 'Mindestens ein Sonderzeichen ist erforderlich.',
				invalidNumber: 'Bitte geben Sie eine gültige Zahl ein. (Beispiel: 14.30, 1, 1,23E+10)',
				invalidDigit: 'Nur Zahlen sind erlaubt.',
				invalidNumberLength: `Die Eingabe muss genau ${errors['invalid-number-length']?.requiredLength} Zeichen lang sein.`,
				invalidHalbWwholeNumber: 'Ungültige Nummer. Muss eine Ganzzahl oder eine halbe Ganzzahl sein.',
				invalidInputAlphanum: 'Nur alphanumerische Zeichen und Punkte sind erlaubt.',
				invalidInputAlpha: 'Nur alphabetische Zeichen und Punkte sind erlaubt.',
				invalidCurrency: 'Bitte geben Sie einen gültigen Wert ein. (z.B. 14.30)',
				required: 'Dieses Feld ist erforderlich.',
				atLeastOneLessonAllowed: 'Mindestens eine Unterrichtsoption muss erlaubt sein.'
			};
			
			return Object.keys(errors).map(key => messages[key] || 'Nicht gültig!').join(' ');
		}
		return "nicht gültig!";
	}
	
	private errorMessage(element: HTMLElement, message: string) {
		this.removeErrorMessage(element);
		const div = document.createElement("div");
		div.classList.add("invalid-feedback");
		const msg = document.createElement("p");
		msg.textContent = message;
		div.appendChild(msg);
		element.parentElement?.appendChild(div);
	}
	
	private removeErrorMessage(element: HTMLElement) {
		const div = element.parentElement?.querySelector(".invalid-feedback");
		if (div) div.remove();
	}
	
	
	
}
