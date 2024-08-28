import {Component, ElementRef, inject, input, OnInit, signal} from '@angular/core';
import {
	CdkCell,
	CdkCellDef,
	CdkHeaderCell,
	CdkHeaderRow,
	CdkHeaderRowDef,
	CdkRow,
	CdkRowDef,
	CdkTable
} from "@angular/cdk/table";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {TooltipModule} from "ngx-bootstrap/tooltip";

import {ResponseService} from "../../../../_services/ResponseService";
import {AuthService} from "../../../../_services/data/auth.service";
import {UserService} from "../../../../_services/data/user.service";
import {FormErrorDirective} from "../../../../_services/directivs/form-error-directive";
import {invokeBlurOnInvalidFormControllers, openFile} from "../../../../_services/utils";
import {LoadingService} from "../../../../_services/LoadingService";
import {Person} from "../../../../types/person";
import {MatTooltip} from "@angular/material/tooltip";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatIcon} from "@angular/material/icon";
import {Invoice} from "../../../../types/Invoice";
import {FileService} from "../../../../_services/data/file.service";
import {InvoicePipe} from "../../../../_services/pipes/InvoicePipe";
import {InvoiceStepPipe} from "../../../../_services/pipes/InvoiceStep";
import {InvoiceStepDirective} from "../../../../_services/directivs/InvoiceStep";
import {InvoiceStepComponent} from "../../invoice-step/invoice-step.component";
import {UploadFileComponent} from "../../upload-file/upload-file.component";
import {FileUploadMetadata} from "../../../../types/FileMetadata";
import {FileCategory} from "../../../../types/enums/FileCategory";
import {ToastrService} from "../../../../_services/ToastrService";
import {DialogService} from "../../../../_services/dialog.service";
import {MatFabButton} from "@angular/material/button";
import {Role} from "../../../../types/enums/role";
import {ContractType} from "../../../../types/enums/ContractType";
import {MatOption} from "@angular/material/autocomplete";
import {Teacher} from "../../../../types/teacher";
import {Student} from "../../../../types/student";
import {MatFormField, MatFormFieldModule} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {
	MatDatepicker,
	MatDatepickerInput,
	MatDatepickerModule,
	MatDatepickerToggle
} from "@angular/material/datepicker";
import moment, {Moment} from "moment";
import {ModuleType} from "../../../../types/enums/module-type";
import {MatSelect} from "@angular/material/select";
import {PdfViewerComponent} from "../../../dialogs/pdf-viewer/pdf-viewer.component";

@Component({
	selector: 'ys-invoice',
	standalone: true,
	imports: [
		CdkCell,
		CdkCellDef,
		CdkHeaderCell,
		CdkHeaderRow,
		CdkHeaderRowDef,
		CdkRow,
		CdkRowDef,
		CdkTable,
		ReactiveFormsModule,
		TooltipModule,
		FormErrorDirective,
		FormsModule,
		MatTooltip,
		MatCheckbox,
		MatIcon,
		InvoicePipe,
		InvoiceStepPipe,
		InvoiceStepDirective,
		InvoiceStepComponent,
		UploadFileComponent,
		MatFabButton,
		MatOption,
		MatFormField,
		MatInput,
		MatDatepickerInput,
		MatDatepickerToggle,
		MatDatepicker,
		MatFormFieldModule,
		MatInputModule,
		MatDatepickerModule,
		FormsModule,
		ReactiveFormsModule,
		MatSelect,
		PdfViewerComponent,
	],
	templateUrl: './invoice.component.html',
	styleUrl: './invoice.component.scss'
})
export class InvoiceComponent implements OnInit {
	minDate = signal<Date>(new Date());
	maxDate = signal<Date>(new Date());
	metadata = signal<FileUploadMetadata>({
		id: 0,
		fileCategory: FileCategory.INVOICE
	});
	
	user = input.required<Teacher |Student>();
	userType = input.required<string>();
	isInvalid = signal<boolean>(false);
	
	invoices = signal<Invoice[]>([]);
	responseService = inject(ResponseService);
	fb = inject(FormBuilder);
	auth = inject(AuthService);
	userService = inject(UserService);
	diaService = inject(DialogService);
	form!: FormGroup;
	el = inject(ElementRef);
	loadingService = inject(LoadingService);
	fileService = inject(FileService);
	toastr = inject(ToastrService);
	
	constructor() {
	
	}
	
	ngOnInit(): void {
		const currentDate = new Date();
		this.minDate().setFullYear(currentDate.getFullYear() - 1);
		this.form = this.fb.group({
			userId: [this.user()!.id, [Validators.required]],
			tax: [false, [Validators.required]],
			date: [null, [Validators.required]],
			saveInvoice: [true, [Validators.required]]
		});
		if(this.userType() == 'student'){
			this.form.addControl('contractId', this.fb.control(null, [Validators.required]))
		}
		this.metadata.set({
			id: this.user().id,
			fileCategory: FileCategory.INVOICE
		});
		this.userService.getInvoices(this.user().id).subscribe({
				next: res => {
					this.invoices.set([...res.data.content]);
				}
			}
		);
		
	}
	
	
	createInvoice() {
		if (this.form.invalid || this.form.pristine) {
			this.isInvalid.set(true);
			invokeBlurOnInvalidFormControllers(this.form, this.el);
			
			this.toastr.warning("", this.form.invalid
				? "Bitte füllen Sie alle erforderlichen Felder aus"
				: "Es gibt keine Änderungen zum Speichern");
			return;
		}
		this.userService.createInvoice(this.form.value, this.userType()).subscribe({
			
			next: async (res) => {
				
				if (this.form.value.saveInvoice && res.data.invoice != null) {
					this.invoices.set([...this.invoices(), res.data.invoice]);
				}
				if (res.data.file != null) {
					this.toastr.success("Erfolgreich", "Rechnung wird in kurze geöffnet");
					
					await openFile(res.data.file, "rechnung-" + new Date().toISOString().slice(0, 10));
				}else{
					this.toastr.error("Fehler", "PDF-Datei wurde nicht generiert. Bitte kontaktieren Sie uns!");
					
				}
			}
		})
	}
	
	
	async onDownloadFile(invoice: Invoice) {
		this.fileService.getFile(invoice.fileId).subscribe({
			next: async (res) => {
				await openFile(res.data, invoice.invoiceNumber);
			},
			error: err => {
				console.error(err);
				
			}
		});
		
	}
	
	odDeleteFile(invoice: Invoice) {
		
		const msg = "Sind Sie sicher, dass Sie diese Rechnung löschen möchten?" +
			" Bitte beachten Sie, dass die Rechnung endgültig gelöscht wird" +
			" und nicht mehr wiederhergestellt werden kann!";
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					this.fileService.deleteFile(invoice.fileId).subscribe({
						next: async (res) => {
							this.toastr.success("Erfolgreich", "Rechnung wurde gelöscht!");
							this.invoices.update((value: Invoice[]) => value.filter(i => i.id !== invoice.id))
						}
					});
				}
			},
			error: (err: any) => console.error(err),
		});
		
	}
	
	
	onFileUploaded(invoice: Invoice) {
		this.invoices.update((invoices: Invoice[]) => [...invoices, invoice]);
		
	}
	
	
	protected readonly Role = Role;
	
	isForStudent() {
	
	}
	
	protected readonly ContractType = ContractType;
	protected readonly Object = Object;
	
	getStudent() {
		return this.user() as Student;
	}
	

	setMonthAndYear(normalizedMonthAndYear: Moment, datepicker: MatDatepicker<Moment>) {
		const ctrlValue = this.form.get('date')?.value ?? moment();
		ctrlValue.month(normalizedMonthAndYear.month());
		ctrlValue.year(normalizedMonthAndYear.year());
		this.form.get('date')?.setValue(ctrlValue);
		datepicker.close();
	}
	
	protected readonly ModuleType = ModuleType;
	

	async onViewPdf(invoice: Invoice) {
		this.fileService.getFile(invoice.fileId).subscribe({
			next: async (res) => {
				this.diaService.viewPdf(res.data, invoice).afterClosed().subscribe({
					next: async (res) => {
					}
				});
			},
			error: err => {
				console.error(err);
				
			}
		});
		
	}
}
