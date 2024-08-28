import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ResponseObject} from "../../types/ResponseObject";
import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";

import {ClientInfo} from "../../types/ClientInfo";
import {ClientInfoService} from "../data/clientInfo.service";
import {catchError} from "rxjs/operators";

@Injectable()
export  class ClientInforesolver implements  Resolve<ResponseObject<ClientInfo> | null>
{

    constructor(private clientInfoService: ClientInfoService,) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<ClientInfo> |null>{
        return this.clientInfoService.getClientInfo().pipe(
            catchError((error) => {
                return of(null);
            }
        ))

    }

}
