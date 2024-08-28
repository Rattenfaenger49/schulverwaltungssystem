import { Injectable } from '@angular/core';
import {ResponseObject} from "../../types/ResponseObject";
import {HttpClientService} from "../http-client-service";

import {ClientInfo} from "../../types/ClientInfo";


@Injectable({
	providedIn: "root",
})
export class ClientInfoService {

	

	constructor(private http: HttpClientService) {
	}
	
	getClientInfo() {
		return this.http.get<ResponseObject<ClientInfo>>("/clientInfo", true);
	}
	
	createClientInfo(data: any) {
		return this.http.post<ResponseObject<ClientInfo>>("/clientInfo", data);
	}
	
	updateClientInfo(data: any) {
		return this.http.put<ResponseObject<ClientInfo>>("/clientInfo", data);
	}
	
	
	getPreferences() {
		return this.http.get<ResponseObject<ClientInfo>>("/clientInfo/preferences");
		
		
	}
}
