import {Observable} from "rxjs";
import {ResponseObject} from "../../types/ResponseObject";
import {Page} from "../../types/page";
import {Student} from "../../types/student";
import {Injectable} from "@angular/core";
import {HttpClientService} from "../http-client-service";
import {PAGE_SIZE} from "./CONST_VAR";
import {Teacher} from "../../types/teacher";

@Injectable({
    providedIn: 'root',
})
export class StudentService {
    private controller = '/students';
    constructor(private http: HttpClientService) {
    }



    getSutdents(query = '',filter = '',page = 0,
                sorting: {key: string, sortDirection: 'asc' | 'desc'} = {key: 'id', sortDirection: 'asc'}
                , pageSize = PAGE_SIZE): Observable<ResponseObject<Page<Student>>> {
        return this.http.get<ResponseObject<Page<Student>>>(this.controller +
            `?query=${query.trim()}&filter=${filter.trim()}&page=${page}&size=${pageSize}&sort=${sorting.key},${sorting.sortDirection}`);
    }
    getStudent(id:string): Observable<ResponseObject<Student>> {
        return this.http.get<ResponseObject<Student>>(this.controller + `/${id}`);
    }


    updateStudent(data: Student) {
        return this.http.put<ResponseObject<any>>(this.controller, data);
    }
    getStudentsFullname(): Observable<ResponseObject<Student[]>> {
        return this.http.get<ResponseObject<Student[]>>(
            this.controller + '/fullnames');
    }



    deleteStudent(id: number) {
        return this.http.delete<ResponseObject<Student>>(this.controller + `/${id}`);
    }
    
    undoDeleteStudent(id: number) {
        return this.http.put<ResponseObject<Teacher>>(this.controller + `/${id}/undo-delete`, null);
        
    }
}


