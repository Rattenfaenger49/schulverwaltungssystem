import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ResponseObject} from "../../types/ResponseObject";
import {Page} from "../../types/page";
import {Lesson} from "../../types/lesson";
import {HttpClientService} from "../http-client-service";
import {PAGE_SIZE} from "./CONST_VAR";
import {InvoiceStatus} from "../../types/enums/InvoiceStatus";
import {Invoice} from "../../types/Invoice";

@Injectable({
  providedIn: "root",
})
export class InvoiceService {

  private controller = "/invoices";
  
  http = inject(HttpClientService);

  updateStatus(invoice: Invoice){
    return this.http.put<ResponseObject<Invoice>>(this.controller + `/${invoice.id}`, invoice);
  }
  
  addSignature(id: number, signature: string) {
    return this.http.put<ResponseObject<Invoice>>(this.controller + `/${id}/signature`, signature);
  }
}
