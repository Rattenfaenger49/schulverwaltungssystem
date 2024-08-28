import {Injectable} from '@angular/core';
import {ResponseObject} from "../../types/ResponseObject";

import {HttpClientService} from "../http-client-service";
import {Student} from "src/app/types/student";
import {Teacher} from "src/app/types/teacher";
import {Admin} from "src/app/types/admin";
import {Observable} from "rxjs";
import {FileMetadata} from "../../types/FileMetadata";
import {BankData} from "../../types/BankData";
import {Invoice} from "../../types/Invoice";


@Injectable({
	providedIn: 'root',
})
export class UserService {
	
	constructor(private http: HttpClientService) {
	}
	
	createPDF(data: any): Observable<ResponseObject<any>> {
		// type is byte[] not provided in ts
		return this.http.get<ResponseObject<any>>(
			`/users/${data['userId']}/document?start=${data['start']}&end=${data['end']}&contractType=${data['contractType']}`);
		
	}
	
	getHolidays() {
		// TODO optimize so that only needed years are requested
		const currentYear = new Date().getFullYear();
		return this.http.get<ResponseObject<any>>(`/dates/feiertage?years=${currentYear - 1},${currentYear},${currentYear + 1}&state=nw&all_states=false`);
		
	}
	
	uploadFile(uploadData: FormData) {
		// NOTE: add or remove gcp-storage to change the storageType
		return this.http.postFile<ResponseObject<FileMetadata |Invoice>>('/files', uploadData);
	}
	
	getUserProfile() {
		return this.http.get<ResponseObject<ResponseObject<Admin | Teacher | Student | any>>>('/users/profile');
	}
	
	updateUserProfile(data: any) {
		return this.http.put<ResponseObject<any>>('/users/profile', data);
		
	}
	
	deleteAccount() {
		return this.http.delete<ResponseObject<any>>('/users/profile');
		
	}
	
	createInvoice(data: any, userType:string) {
		return this.http.post<ResponseObject<any>>(
			`/invoices/${userType}`, data);
	}
	
	getInvoices(userId: number) {
      return this.http.get<ResponseObject<BankData>>(`/invoices?userId=${userId}`);
      
    }
	
	getPersonaFiles(userId: number) {
		return this.http.get<ResponseObject<BankData>>(`/users/files?userId=${userId}`);
		
	}
	
	getBankInfo(userId: number) {
		return this.http.get<ResponseObject<BankData>>(`/users/bankdata/${userId}`);
		
	}
	
	saveBankInfo(value: any){
		return this.http.post(`/users/bankdata`, value);
	}
}
