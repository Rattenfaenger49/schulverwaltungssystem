import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {UserService} from "../data/user.service";
import {ResponseObject} from "../../types/ResponseObject";
import {Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {Contract} from "../../types/contract";
import {ContractService} from "../data/contract.service";
import {DialogService} from "../dialog.service";
import {catchError} from "rxjs/operators";

@Injectable()
export  class ContractResolver implements  Resolve<ResponseObject<Contract>>
{

    constructor(private contractService: ContractService,
                private router:Router,
                private diaService: DialogService) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Contract>>{
    const id = Number(route.paramMap.get('id'));
    if (id) {
        return this.contractService.getContract(id);
    }else {
        this.router.navigate(['/error']); // Redirect to an error page
        return EMPTY; // or any other default value or error handling mechanism
    }
    }

}
