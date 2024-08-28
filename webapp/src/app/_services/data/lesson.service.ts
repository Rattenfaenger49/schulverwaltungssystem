import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {ResponseObject} from "../../types/ResponseObject";
import {Page} from "../../types/page";
import {Lesson, LessonVersion} from "../../types/lesson";
import {HttpClientService} from "../http-client-service";
import {PAGE_SIZE} from "./CONST_VAR";

@Injectable({
  providedIn: "root",
})
export class LessonService {

  private controller = "/lessons";

  constructor(private http: HttpClientService) {}

  getLessons(
      filter: {query:string, userId: any, param: string} = {query: "", userId: "", param: ''},
      sorting: {key: string, sortDirection: 'asc'| 'desc'} = {key: 'id', sortDirection: 'asc'},
      page = 0,
      pageSize = PAGE_SIZE,
      sortDirection = "id",
  ): Observable<ResponseObject<Page<Lesson>>> {
    return this.http.get<ResponseObject<Page<Lesson>>>(
        this.controller +
        `?query=${filter.query}&id=${filter.userId}&filter=${filter.param}&page=${page}&size=${pageSize}&sort=${sorting.key},${sorting.sortDirection}`,
    );
  }
  getLesson(id: number): Observable<ResponseObject<Lesson>> {
    return this.http.get<ResponseObject<Lesson>>(this.controller + `/${id}`);
  }
  createLesson(data: any): Observable<ResponseObject<Lesson>> {
    return this.http.post<ResponseObject<Lesson>>( this.controller, data);
  }
  updateLseeon(data: any): Observable<ResponseObject<Lesson>> {

    return this.http.put<ResponseObject<Lesson>>( this.controller , data);
  }

  deleteLesson(id: number) {
    return this.http.delete<ResponseObject<Lesson>>( this.controller + `/${id}`);
  }

  addSignature(signautre: any) {
    return this.http.post<ResponseObject<Lesson>>(
      "/lessons/" + signautre['lessonId'] + "/signature",
        signautre
    );
  }
  // TODO remove
  getLessonFile(file: any) {
    return this.http.get<ResponseObject<string>>(
        this.controller +
        "/" + file["id"] + "/files/" + file["id"],
    );
  }
  
  
  getLessonVersions(lessonId: number) {
    return this.http.get<ResponseObject<LessonVersion>>(this.controller + "/" + lessonId+ "/archive");
  }
}
