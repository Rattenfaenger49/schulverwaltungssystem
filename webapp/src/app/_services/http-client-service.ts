import {Injectable} from '@angular/core';
import {HttpClient, HttpContext, HttpEvent} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {SkipDialogResponse} from "./skip-dialog-response";
import {SkipLoading} from "../components/shared/loader/skip.loading";


const API_URL = environment.apiUrl + "/api/v1";

@Injectable({
	providedIn: 'root',
})
export class HttpClientService {
	
	constructor(private http: HttpClient) {
	
	}
	
	get<T>(url: string, skipDialog = false, skipLoading = false): Observable<T> {
		return this.http.get<T>(API_URL + url,
			{
				context:
					new HttpContext().set(SkipDialogResponse, skipDialog).set(SkipLoading, skipLoading),
				
			});
	}
	
	post<T>(url: string, data: any, skipDialog = false, skipLoading = false): Observable<T> {
		return this.http.post<T>(API_URL + url, data
		,{
			context:
				new HttpContext().set(SkipDialogResponse, skipDialog).set(SkipLoading, skipLoading)
		});
	}
	
	put<T>(url: string, data: any): Observable<T> {
		return this.http.put<T>(API_URL + url, data);
	}
	
	delete<T>(url: string): Observable<T> {
		return this.http.delete<T>(API_URL + url);
	}
	
	postFile<T>(url: string, data: FormData): Observable<HttpEvent<T>> {
		// Set content type to multipart/form-data
		return this.http.post<T>(API_URL + url, data, {
			responseType: 'json',
			reportProgress: true,
			observe: 'events',
		});
		
	}
}
