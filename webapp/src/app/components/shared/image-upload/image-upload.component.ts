import {Component, effect, ElementRef, model, ViewChild} from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {JsonPipe, NgClass, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";


@Component({
	selector: 'ys-image-upload',
	standalone: true,
	imports: [
		NgIf,
		NgClass,
		ReactiveFormsModule,
		MatIcon,
		JsonPipe,
		MatButton
	],
	templateUrl: './image-upload.component.html',
	styleUrl: './image-upload.component.scss'
})
export class ImageUploadComponent {
	@ViewChild('uploadImage') uploadImageInput!: HTMLInputElement;
	@ViewChild('uploadedImage') uploadedImage!: ElementRef<HTMLImageElement>;
	tempImg = '';
	image = model<string>();
	
	constructor() {
		effect(() => {
			this.uploadedImage.nativeElement.src = this.image() ?? '';
		});
	}
	
	uploadHandler(event: any): void {
		const input = event?.target as HTMLInputElement;
		const file = input.files?.[0];
		
		if (file) {
			const reader = new FileReader();
			reader.onload = () => {
				if (typeof reader.result === "string") {
					// Set the image source for displaying
					if (this.uploadedImage) {
						this.uploadedImage.nativeElement.src = reader.result;
						this.image.set(reader.result);
					}
					// Extract the base64 part and store it
					this.tempImg = reader.result.split(',')[1];
				} else {
				}
			};
			
			reader.readAsDataURL(file);
			
		}
	}
	
	
}
