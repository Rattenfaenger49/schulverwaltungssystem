import {Component, ElementRef, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {AuthService} from '../../../_services/data/auth.service';
import {Router, RouterLink} from "@angular/router";
import {FormErrorDirective} from '../../../_services/directivs/form-error-directive';
import {FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators,} from '@angular/forms';
import {AsyncPipe, JsonPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {ValidationPatterns} from "../../../validators/custom-validations";
import {MatIconModule} from '@angular/material/icon'
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {MatInput, MatPrefix} from "@angular/material/input";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {invokeBlurOnInvalidFormControllers} from "../../../_services/utils";
import {ToastrService} from "../../../_services/ToastrService";
import {CountdownPipe} from "../../../_services/pipes/CountdownPipe ";
import {interval, Subscription} from "rxjs";
import {MatButton} from "@angular/material/button";


@Component({
	selector: "ys-login",
	templateUrl: "./login.component.html",
	styleUrls: ["./login.component.scss"],
	standalone: true,
	imports: [
		FormsModule,
		FormErrorDirective,
		NgClass,
		AsyncPipe,
		RouterLink,
		NgOptimizedImage,
		ReactiveFormsModule,
		JsonPipe,
		MatIconModule,
		MatAutocompleteTrigger,
		MatInput,
		LoaderComponent,
		NgIf,
		MatPrefix,
		CountdownPipe,
		MatButton,
	],
})
export class LoginComponent implements OnInit , OnDestroy{
	
	
	toastr = inject(ToastrService);
	auth = inject(AuthService);
	fb = inject(FormBuilder);
	router = inject(Router);
	el = inject(ElementRef);
	unlockTime = signal<number>(0);
	isSubmitted = signal(false);
	private countdownSubscription: Subscription | null = null;
	constructor() {
		const schoolId = this.auth.getSchoolId();
		
		if (!schoolId) {
			this.hasProvidedSchoolId = false;
		} else {
			this.hasProvidedSchoolId = true;
			this.form.get('schoolId')?.setValue(schoolId);
			this.form.get('schoolId')?.disable();
		}
		
		
	}

	passowrdType = "password";
	form = this.fb.group({
		schoolId: ['', [Validators.required]],
		username: [
			"",
			[Validators.required, Validators.pattern(ValidationPatterns.email)],
		],
		password: ["", [Validators.required, this.passwordValidationFn]],
	});
	isLoginFailed = false;
	errorMessage = "";
	hasProvidedSchoolId = false;
	err: any;
	
	passwordValidationFn(
		control: FormControl,
	): { [key: string]: boolean } | null {
		const password = control.value;
		const errors: any = {};
		
		if (!/\d/.test(password)) {
			errors.hasNumber = true;
		}
		if (!/[A-Z]/.test(password)) {
			errors.hasUpper = true;
		}
		if (!/[a-z]/.test(password)) {
			errors.hasLower = true;
		}
		if (!/[^a-zA-Z\d]+/.test(password)) {
			errors.hasSpecialCharacter = true;
		}
		
		return Object.keys(errors).length !== 0 ? errors : null;
	}
	
	
	ngOnInit(): void {
	}
	
	login(): void {
		
		this.isSubmitted.set(true);
		if (this.form.invalid || !this.form.getRawValue().schoolId) {
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern");
			
			return;
		}
		this.auth.saveSchoolId(this.form.getRawValue().schoolId as string);
		this.unlockTime.set(0);
		this.stopCountdown();
		this.auth
			.login(this.form.value)
			.subscribe({
				next: (data) => {
					this.auth.loginHandler(data);
					this.router.navigate(["/dashboard"]);
				},
				error: (err) => {
					if (err.status === 429) {
						// Customize the error message based on the backend response
						 this.getCustomErrorMessage(err.error.message);
						this.startCountdown();
					} else {
						console.error("err", err);
						this.errorMessage = err.error.message;
	
					}
					this.err = err;
					this.isLoginFailed = true;
				},
			});
	}
	
	// Example of a POST request. Note: data
	// can be passed as a raw JS Object (must be JSON serializable)
	
	
	togglePasswordVisibility() {
		this.passowrdType = this.passowrdType === "password" ? "text" : "password";
	}
	

	changeSchoolId() {
		this.form.get('schoolId')?.enable();
	}
	
	private getCustomErrorMessage(message:string) {
		const lockoutEndStr = message.split('Abgesperrt at: ')[1];
		if (lockoutEndStr) {
			const unlockDate = new Date(lockoutEndStr);
			const now = new Date();
	
			this.unlockTime.set(this.getTimeDifference(now, unlockDate));
			

		} else {
			this.errorMessage = 'Login failed due to too many failed attempts.';
		}
	}
	getTimeDifference(startDate: Date, endDate: Date): number {
		// Calculate the difference in milliseconds
		return  endDate.getTime() - startDate.getTime();
		/*
		// Convert milliseconds to seconds
		const diffInSec = Math.max(Math.floor(diffInMs / 1000), 0);
		
		// Calculate hours, minutes, and seconds
		const hours = Math.floor(diffInSec / 3600);
		const minutes = Math.floor((diffInSec % 3600) / 60);
		const seconds = diffInSec % 60;
		
		// Format as hh:mm:ss
		const formattedTime = [
			String(hours).padStart(2, '0'),
			String(minutes).padStart(2, '0'),
			String(seconds).padStart(2, '0')
		].join(':');
		
		return formattedTime;*/
	}
	private startCountdown(): void {
		this.countdownSubscription = interval(1000).subscribe(() => {
			if (this.unlockTime() <= 0) {
				this.unlockTime.set(0);
				this.stopCountdown();
			} else {
				this.unlockTime.update(x => x- 1000);
			}
		});
	}
	private stopCountdown(): void {
		if (this.countdownSubscription) {
			this.countdownSubscription.unsubscribe();
		}
	}
	ngOnDestroy(): void {
		// Clean up the countdown subscription
		if (this.countdownSubscription) {
			this.countdownSubscription.unsubscribe();
		}
	}
}
