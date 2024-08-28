import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ResponseObject} from "../../types/ResponseObject";
import {Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {Institution} from "../../types/Institution";
import {InstitutionService} from "../data/institution.service";

@Injectable()
export  class InstitutionResolver implements  Resolve<ResponseObject<Institution>>
{

    constructor(private institutionService: InstitutionService,
                private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Institution>>{
    const id = Number(route.paramMap.get('id'));
    if (id) {
        return this.institutionService.getInstitution(id);
    }else {
        this.router.navigate(['/error']); // Redirect to an error page
        return EMPTY; // or any other default value or error handling mechanism
    }
    }

}
