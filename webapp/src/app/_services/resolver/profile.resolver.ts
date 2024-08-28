import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ResponseObject} from "../../types/ResponseObject";
import {Injectable} from "@angular/core";
import { EMPTY, Observable, catchError } from "rxjs";

import {UserService} from "../data/user.service";
import {Admin} from "../../types/admin";
import {Teacher} from "../../types/teacher";
import {Student} from "../../types/student";

@Injectable()
export  class ProfileResolver implements  Resolve<ResponseObject<Admin| Teacher | Student | any>>
{

    constructor(private userService: UserService,
                private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Admin| Teacher | Student | any>>{
       return this.userService.getUserProfile()
            .pipe(
            catchError((err) => {
                console.error("Error in profile resolver", err)
                return EMPTY;
            }
        ));
    }

}
