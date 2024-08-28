import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {ResponseObject} from "../../types/ResponseObject";
import {Contract} from "../../types/contract";
import {Page} from "../../types/page";
import {HttpClientService} from "../http-client-service";
import {PAGE_SIZE} from "./CONST_VAR";
import {ContractType} from "../../types/enums/ContractType";
import {ContractStatus} from "../../types/enums/ContractStatus";


@Injectable({
	providedIn: 'root',
})
export class ContractService {
	
	private controller = '/contracts';
	
	constructor(
		private http: HttpClientService
	) {
	}
	
	getContract(id: number): Observable<ResponseObject<Contract>> {
		
		return this.http.get<ResponseObject<Contract>>(this.controller + `/${id}`);
	}
	
	getContracts(query = '',
				 contractType: ContractType,
				 contractStatus: ContractStatus,
				 sorting: { key: string, sortDirection: 'asc' | 'desc' } = {key: 'id', sortDirection: 'asc'},
				 page = 0, pageSize = PAGE_SIZE, sortDirection = 'startAt'):
		Observable<ResponseObject<Page<Contract>>> {
		
		return this.http.get<ResponseObject<Page<Contract>>>(this.controller +
			`?query=${query}&page=${page}&contractStatus=${contractStatus}&contractType=${contractType}&size=${pageSize}&sort=${sorting.key},${sorting.sortDirection}`);
		
	}
	
	getContractsWithQuery(query: string, page = 0, pageSize = PAGE_SIZE, sortDirection = 'firstName'):
		Observable<ResponseObject<Page<Contract>>> {
		return this.http.get<ResponseObject<Page<Contract>>>(
			this.controller + `/search?query=${query}&` + 'page=' + page + '&size=' + pageSize + '&sortDirection=' + sortDirection);
	}
	
	updateContract(data: any) {
		return this.http.put<ResponseObject<Contract>>(this.controller + `/${data.id}`, data);
		
	}
	
	createContract(data: any) {
		return this.http.post<ResponseObject<Contract>>(this.controller, data)
			;
		
	}
	
	deleteContract(id: number) {
		return this.http.delete<ResponseObject<Contract>>(this.controller + `/${id}`);
		
	}
}

