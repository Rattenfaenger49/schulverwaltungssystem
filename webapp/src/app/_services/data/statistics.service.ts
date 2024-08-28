import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {ResponseObject} from "../../types/ResponseObject";

import {HttpClientService} from "../http-client-service";
import {AdminStatistic, StudentsStatistic, TeachersStatistic} from "../../types/app-statistics";



@Injectable({
    providedIn: 'root',
})
export class StatisitcsService {
    private controller = '/statisitcs';

    constructor( private http: HttpClientService) {
    }
    adminStatistic(): Observable<ResponseObject<AdminStatistic>> {

        return this.http.get<ResponseObject<AdminStatistic>>(
            this.controller+'/admin' );

    }
    teacherStatistic(): Observable<ResponseObject<TeachersStatistic>> {

        return this.http.get<ResponseObject<TeachersStatistic>>(
            this.controller+'/teacher' );

    }

    studentStatistics(): Observable<ResponseObject<StudentsStatistic>> {

        return this.http.get<ResponseObject<StudentsStatistic>>(
            this.controller+'/student' );

    }

}
