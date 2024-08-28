import {Component, ElementRef, inject, input, OnInit, signal} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Person} from "../../../../types/person";
import {BankDataComponent} from "../../bank-daten/bank-daten.component";
import {FormErrorDirective} from "../../../../_services/directivs/form-error-directive";
import {InvoicePipe} from "../../../../_services/pipes/InvoicePipe";
import {InvoiceStepComponent} from "../../invoice-step/invoice-step.component";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatIcon} from "@angular/material/icon";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {UploadFileComponent} from "../../upload-file/upload-file.component";
import {FileMetadata, FileUploadMetadata} from "../../../../types/FileMetadata";
import {FileCategory} from "../../../../types/enums/FileCategory";
import {ResponseService} from "../../../../_services/ResponseService";
import {AuthService} from "../../../../_services/data/auth.service";
import {UserService} from "../../../../_services/data/user.service";
import {DialogService} from "../../../../_services/dialog.service";
import {LoadingService} from "../../../../_services/LoadingService";
import {FileService} from "../../../../_services/data/file.service";
import {openFile} from "../../../../_services/utils";
import {ToastrService} from "../../../../_services/ToastrService";

@Component({
  selector: 'ys-personal',
  standalone: true,
	imports: [
		BankDataComponent,
		FormErrorDirective,
		FormsModule,
		InvoicePipe,
		InvoiceStepComponent,
		MatCheckbox,
		MatIcon,
		ReactiveFormsModule,
		TooltipModule,
		UploadFileComponent
	],
  templateUrl: './personal.component.html',
  styleUrl: './personal.component.scss'
})
export class PersonalComponent implements  OnInit{
  form!: FormGroup;
  user = input.required<Person>();
	metadata = signal<FileUploadMetadata>({
		id: 0,
		fileCategory: FileCategory.INVOICE
	});
	
	
	files = signal<FileMetadata[]>([]);
	responseService = inject(ResponseService);
	fb = inject(FormBuilder);
	auth = inject(AuthService);
	userService = inject(UserService);
	diaService = inject(DialogService);
	el = inject(ElementRef);
	loadingService = inject(LoadingService);
	fileService = inject(FileService);
	toastr = inject(ToastrService);
	
	ngOnInit(): void {
		
		this.metadata.set({
			id: this.user().id,
			fileCategory: FileCategory.PERSONAL_FILE
		});
		this.userService.getPersonaFiles(this.user().id).subscribe({
				next: res => {
					if(res.data.length > 0)
						this.files.set([...res?.data]);
				}
			}
		);
		
	}
	async downloadFile(file: FileMetadata) {
		this.fileService.getFile(file.id).subscribe({
			next: async (res) => {
				await openFile(res.data, file.fileName, file.fileType);
			}
		});
		
	}
	
	deleteFile(file: FileMetadata) {
		
		const msg = "Sind Sie sicher, dass Sie diese Datei löschen möchten?" +
			" Bitte beachten Sie, dass die Datei endgültig gelöscht wird" +
			" und nicht mehr wiederhergestellt werden kann!";
		this.diaService.confirmationDialog(msg).subscribe({
			next: (res: any) => {
				if (res) {
					this.fileService.deleteFile(file.id).subscribe({
						next: async (res) => {
							this.responseService.responseDialog(res, true);
							this.files.update((value: FileMetadata[] )=> value.filter(i => i.id !== file.id))
						}
					});
				}
			},
			error: (err: any) => console.error(err),
		});
		
	}
	

	
	fileUploaded(file: FileMetadata) {
		this.files.update((files: FileMetadata[]) => [...files, file]);
		
	}
}
