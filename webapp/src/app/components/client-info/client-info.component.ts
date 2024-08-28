import {ChangeDetectionStrategy, Component, effect, ElementRef, inject, model, OnInit, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ClientInfoService} from "../../_services/data/clientInfo.service";
import {ClientInfo} from "../../types/ClientInfo";
import {AddressComponent} from "../shared/address/address.component";
import {FormErrorDirective} from "../../_services/directivs/form-error-directive";
import {MatInput, MatSuffix} from "@angular/material/input";
import {UploadFileComponent} from "../shared/upload-file/upload-file.component";
import {FileUploadMetadata} from "../../types/FileMetadata";
import {FileCategory} from "../../types/enums/FileCategory";
import {ToastrService} from "../../_services/ToastrService";
import {MatButton} from "@angular/material/button";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {DialogService} from "../../_services/dialog.service";
import {AuthService} from "../../_services/data/auth.service";
import {LoaderComponent} from "../shared/loader/loader.component";
import {ImageUploadComponent} from "../shared/image-upload/image-upload.component";
import {invokeBlurOnInvalidFormControllers} from "../../_services/utils";
import {MatIcon} from "@angular/material/icon";
import {ActivatedRoute} from "@angular/router";
import {ibanValidator} from "../../_services/iban-validator";
import {regexValidator, ValidationPatterns} from "../../validators/custom-validations";
import {PreferencesComponent} from "./preferences/preferences.component";
import {MatTab, MatTabGroup} from "@angular/material/tabs";

@Component({
	selector: 'ys-client-info',
	standalone: true,
	imports: [
		ReactiveFormsModule,
		AddressComponent,
		FormErrorDirective,
		MatInput,
		UploadFileComponent,
		MatButton,
		MatChip,
		MatChipSet,
		LoaderComponent,
		ImageUploadComponent,
		MatIcon,
		MatSuffix,
		PreferencesComponent,
		MatTabGroup,
		MatTab
	],
	templateUrl: './client-info.component.html',
	styleUrl: './client-info.component.scss',
	changeDetection: ChangeDetectionStrategy.OnPush
	
})
export class ClientInfoComponent{
	
	fb = inject(FormBuilder);
	toastr = inject(ToastrService);
	diaService = inject(DialogService);
	route = inject(ActivatedRoute);
	
	auth = inject(AuthService);
	clientInfoService = inject(ClientInfoService);
	el = inject(ElementRef);
	form!: FormGroup;
	clientInfo = signal<ClientInfo | null>(null);
	isInvalid = signal<boolean>(false);
	metadata = signal<FileUploadMetadata>(
		{
			id: this.auth.getClaims()!.id,
			fileCategory: FileCategory.LOGO
		});
	logo = signal<string>('');
	files = model<File[]>([]);
	imagePreviewUrl!: any;
	
	constructor() {
		this.clientInfo.set(this.route.snapshot?.data["clientInfo"]?.data);
		effect(() => {
			this.generateImagePreview(this.files()[0]);
		});
		this.initializeForm();

	}

	
	private initializeForm() {
		this.logo.set(this.clientInfo()?.logo ?? '');
		
		this.form = this.fb.group({
			id: [this.clientInfo()?.id ?? null],
			companyName: [this.clientInfo()?.companyName, [Validators.required]],
			abbreviation: [this.clientInfo()?.abbreviation , [Validators.required]],
			firstName: [this.clientInfo()?.firstName , [Validators.required]],
			lastName: [this.clientInfo()?.lastName , [Validators.required]],
			bankData: this.fb.group({
				id: [this.clientInfo()?.bankData?.id ],
				bankName: [this.clientInfo()?.bankData?.bankName ],
				bic: [this.clientInfo()?.bankData?.bic ],
				iban: [this.clientInfo()?.bankData?.iban , [ibanValidator()]],
				accountHolderName: [this.clientInfo()?.bankData?.accountHolderName ?? null]
			}),
			email: [this.clientInfo()?.email , [Validators.required, Validators.pattern(ValidationPatterns.email)]], // email validation
			supportEmail: [this.clientInfo()?.supportEmail , [Validators.required, Validators.pattern(ValidationPatterns.email)]], // email validation
			phone: [this.clientInfo()?.phone , [Validators.required]],
			supportPhone: [this.clientInfo()?.supportPhone, [Validators.required, Validators.pattern(ValidationPatterns.phoneNumber)] ],
			address: [this.clientInfo()?.address],
			website: [this.clientInfo()?.website, [Validators.pattern('https://.+')]], // URL validation
			taxNumber: [this.clientInfo()?.taxNumber, [regexValidator(/^DE\d{10,11}$/)]],
			logo: [this.clientInfo()?.logo],
			privacyPolicyUrl: [this.clientInfo()?.privacyPolicyUrl , [Validators.pattern('https://.+')]], // URL validation
			preferences: [this.clientInfo()?.preferences ?? this.getDefaultPreferences(), [Validators.required] ],
			extras: [this.clientInfo()?.extras ],
		});
	}
	
	onSubmit(): void {
		if (this.form.invalid || this.form.pristine) {
			this.isInvalid.set(true);
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			invokeBlurOnInvalidFormControllers(this.form.get('bankData') as FormGroup, this.el);
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern",);
			return;
		}
		
		this.form.value.logo = this.logo();
		if (this.clientInfo()?.id) {
			this.clientInfoService.updateClientInfo(this.form.value).subscribe({
				next: value => {
					this.toastr.success("", "Anfrage wurde erfolgreich bearbeitet");
					this.form.markAsPristine();
				}
			});
		} else {
			this.clientInfoService.createClientInfo(this.form.value).subscribe({
				next: value => {
					this.toastr.success("", "Anfrage wurde erfolgreich bearbeitet");
					this.form.markAsPristine();
					
				}
			});
		}
		
	}
	
	private generateImagePreview(file: File): void {
		if (file) {
			const reader = new FileReader();
			
			reader.onload = () => {
				this.imagePreviewUrl = reader.result;
			};
			
			reader.readAsDataURL(file); // Convert file to base64 string
			
		}
	}
	
	onSchowInfo(msg:string) {
		this.diaService.confirmationDialog(msg, 'info');
	}
	private getDefaultPreferences() {
		return {
			allowTeacherBillGeneration: true,
			allowStudentBillGeneration: true,
			emailNotificationsForAppointments: true,
			emailNotificationForLessonChanges: true,
			emailNotificationForClientInfoChanges: true
		};
	}

}
