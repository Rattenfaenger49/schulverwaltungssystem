import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ResponseObject} from "../../types/ResponseObject";
import {Page} from "../../types/page";
import {Lesson} from "../../types/lesson";
import {HttpClientService} from "../http-client-service";
import {PAGE_SIZE} from "./CONST_VAR";
import {ToastrService} from "../ToastrService";

@Injectable({
  providedIn: "root",
})
export class FileService {

  private controller = "/files";
  
  http = inject(HttpClientService);

  
  getLessonFile(file: any) {
    return this.http.get<ResponseObject<string>>(
        this.controller +
        "/" + file["id"],
    );
  }
  toastr = inject(ToastrService);
  deleteFile(fileId: number | undefined) {
    if(fileId === undefined) {
      this.toastr.error("Error", "Etwas ist schiefgelaufen!")
    }
    return this.http.delete<ResponseObject<string>>(this.controller + `/${fileId}`);
  }
  
  getFile(id: number) {

    return this.http.get<ResponseObject<string>>(
        this.controller +
        "/" + id
    );
  }
}
