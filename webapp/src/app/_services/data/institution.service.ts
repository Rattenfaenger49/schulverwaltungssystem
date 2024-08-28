import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {ResponseObject} from "../../types/ResponseObject";
import {Page} from "../../types/page";
import {HttpClientService} from "../http-client-service";
import {Institution} from "../../types/Institution";
import {PAGE_SIZE} from "./CONST_VAR";



@Injectable({
    providedIn: 'root',
})
export class InstitutionService {
    private controller = '/institutions';

    constructor( private http: HttpClientService) {
    }
    getInstitutions(query= '', page = 0, pageSize = PAGE_SIZE, sortDirection = 'name'): Observable<ResponseObject<Page<Institution>>>{

        return this.http.get<ResponseObject<Page<Institution>>>('/institutions' +
            `?query=${query}&page=${page}&size=${pageSize}&sortDirection=${sortDirection}`);

    }
    getInstitutionsWithQuery(query: string, page = 0, pageSize= PAGE_SIZE, sortDirection= 'firstName'):
        Observable<ResponseObject<Page<Institution>>>  {
// TODO implement logic on Server
        return this.http.get<ResponseObject<Page<Institution>>>(
            `/institutions/search?query=${query}&page=${page}&size=${pageSize}&sortDirection=${sortDirection}`);
    }
    getInstitution(id: number): Observable<ResponseObject<Institution>> {
        return this.http.get<ResponseObject<Institution>>(`${this.controller}/${id}`);
    }
    createInstitution(data:any): Observable<ResponseObject<any>> {
        return this.http.post<ResponseObject<any>>(this.controller, data );
    }

    updateInstitution(data: any): Observable<ResponseObject<Institution>> {
        return this.http.put<ResponseObject<Institution>>(this.controller ,data);
    }


    deleteInstitution(id: number) {
        return this.http.delete<ResponseObject<any>>(`${this.controller}/${id}`);
    }
}
