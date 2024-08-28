import {FormGroup} from "@angular/forms";
import {ElementRef} from "@angular/core";
import {Device} from "@capacitor/device";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capawesome-team/capacitor-file-opener";
import {document} from "ngx-bootstrap/utils";

export function parseDateTimeStringFromDate(date: Date | undefined): { date: Date | null; time: string } {
	let date1 = null;
	let time = "";
	if (date) {
		date1 = date;
		const hours = date.getHours() < 10 ? `0${date.getHours()}` : date.getHours();
		const minutes = date.getMinutes() < 10 ? `0${date.getMinutes()}` : date.getMinutes();
		time = `${hours}:${minutes}`;
	}
	
	return {date: date1, time: time};
	
}

export function toLocalDate(startDate: string): Date {
	const utcDate = new Date(startDate); // Annahme: Datum in UTC vom Server
	
	const gmtOffset = 2 * 60 * 60 * 1000;
	return new Date(utcDate.getTime() + gmtOffset);
}

export function invokeBlurOnInvalidFormControllers(form: FormGroup, el: ElementRef) {
	Object.keys(form.controls).forEach(controlName => {
		const control = form.get(controlName);
		if (control && control.invalid) {
			const element = el.nativeElement.querySelector(`[formControlName="${controlName}"]`);
			if (element) {
				const event = new Event('blur', {
					bubbles: true,
					cancelable: true
				});
				element.dispatchEvent(event);
			}
		}
	});
}

export async function openFile(data: string | Blob, fileName = 'file', fileType = 'application/pdf') {
	
	const device = await Device.getInfo();
	if (device.platform !== "web") {
		try {
			
			const filePath = `/${fileName}`;
			
			const status = await Filesystem.checkPermissions();
			if (status.publicStorage !== 'granted') {
				const permissionStatus = await Filesystem.requestPermissions();
				if (permissionStatus.publicStorage !== 'granted') {
					
					return;
				}
			}
			
			await Filesystem.writeFile({
				path: filePath,
				data: data,
				directory: Directory.Documents
			});
			const uri = await Filesystem.getUri({
				directory: Directory.Documents,
				path: filePath
			});
			
			await FileOpener.openFile(
					{
						path: uri.uri,
						mimeType: fileType
					})
				.then(() => console.debug('File opened'))
				.catch((error: any) => console.error('Error opening file', error));
			
		} catch (e) {
			console.error("Unable to write file", e);
			
		}
	} else {
		const byteCharacters = atob(data as string);
		const byteNumbers = new Array(byteCharacters.length);
		for (let i = 0; i < byteCharacters.length; i++) {
			byteNumbers[i] = byteCharacters.charCodeAt(i);
		}
		const byteArray = new Uint8Array(byteNumbers);
		const blob = new Blob([byteArray], {type: fileType});
		
		// Create an <a> element
		const a = document.createElement('a');
		a.href = window.URL.createObjectURL(blob);
		
		// Set the download attribute with the desired filename
		a.download = fileName;
		
		// Programmatically trigger a click on the <a> element to open the file
		a.click();
	}
}
