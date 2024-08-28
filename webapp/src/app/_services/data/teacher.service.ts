import {Injectable} from "@angular/core";
import {Teacher} from "../../types/teacher";
import {ResponseObject} from "../../types/ResponseObject";
import {Observable} from "rxjs";
import {Page} from "../../types/page";
import {HttpClientService} from "../http-client-service";
import { HttpHeaders } from "@angular/common/http";
import { PAGE_SIZE } from "./CONST_VAR";


@Injectable({
    providedIn: 'root',
})
export class TeacherService{

    private controller = '/teachers';
    constructor(private http: HttpClientService) {
    }

    getTeachers(query: string  = '',filter= '',page = 0,
                sorting: {key: string, sortDirection: 'asc' | 'desc'} = {key: 'id', sortDirection: 'asc'}
                , pageSize = PAGE_SIZE, sortDirection = 'firstName'): Observable<ResponseObject<Page<Teacher>>>{
        return this.http.get<ResponseObject<Page<Teacher>>>(this.controller +
            `?query=${query.trim()}&filter=${filter.trim()}&page=${page}&size=${pageSize}&sort=${sorting.key},${sorting.sortDirection}` );
    }

    getTeacherById(id: number): Observable<ResponseObject<Teacher>>{

        return this.http.get<ResponseObject<Teacher>>(this.controller +`/${id}`);
    }



    updateTeacher(data: any): Observable<ResponseObject<Teacher>> {
            return this.http.put<ResponseObject<Teacher>>
            (this.controller, data);
    }

    getTeachersFullname(): Observable<ResponseObject<Teacher[]>> {
        return this.http.get<ResponseObject<Teacher[]>>(
            this.controller + '/fullnames');
    }

    assignStudentToTeacher(teacherId:number, studentId:number):Observable<ResponseObject<Teacher>>  {
        return this.http.post<ResponseObject<Teacher>>(this.controller + `/${teacherId}/${studentId}`,null);

    }

    removeStudentAssignment(teacherId: string, studentId: string) {
        return this.http.delete<ResponseObject<Teacher>>(this.controller + `/${teacherId}/${studentId}`);
    }

    deleteTeacher(id: string) {
        return this.http.delete<ResponseObject<Teacher>>(this.controller + `/${id}`);

    }
    undoDeleteTeacher(id: string) {
        return this.http.put<ResponseObject<Teacher>>(this.controller + `/${id}/undo-delete`, null);
    }

    createDocs(data: any ) {
        let url = this.controller +
            `/${data['userId']}/document?start=${data['start']}&end=${data['end']}`;

        if (data['studentId'] !== '' && data['studentId'] !== null) {
            url += `&studentId=${data['studentId']}`;
        }
        return this.http.get<ResponseObject<any>>(url);
    }
}
