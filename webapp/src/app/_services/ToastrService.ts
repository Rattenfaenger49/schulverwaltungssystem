import {inject, Injectable} from "@angular/core";
import {IndividualConfig, ToastrService as Toastr} from "ngx-toastr";

@Injectable({
  providedIn: "root",
})
export class ToastrService {
  
  #toastr = inject(Toastr)
  #option:Partial<IndividualConfig<any>> = {
    closeButton: true,
    progressBar: true,
    progressAnimation: 'increasing',
    enableHtml: true,
    easeTime: 500,
    disableTimeOut: false,
    easing: 'ease-in-out',
    positionClass: 'toast-top-right',
  };
  success(title: string, msg: string) {
    this.#toastr.success(msg,title, this.#option);
  }
  info(title: string, msg: string) {
    this.#toastr.info(msg,title,  this.#option);
  }
  warning(title: string, msg: string) {
    this.#toastr.warning(msg,title,  this.#option);
  }
  error(title: string, msg: string) {
    this.#toastr.error( msg, title, this.#option);
  }
}
