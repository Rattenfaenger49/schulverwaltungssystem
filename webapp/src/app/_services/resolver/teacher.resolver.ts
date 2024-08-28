import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {UserService} from "../data/user.service";
import {ResponseObject} from "../../types/ResponseObject";
import {Teacher} from "../../types/teacher";
import {TeacherService} from "../data/teacher.service";

@Injectable()
export  class TeacherResolver implements  Resolve<ResponseObject<Teacher>>
{

    constructor(
        private teacherService: TeacherService,
                private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Teacher>>{
    const id = Number(route.paramMap.get('id'));
    if (id) {
        return this.teacherService.getTeacherById(id);
    }else {
        this.router.navigate(['/error']); // Redirect to an error page
        return EMPTY; // or any other default value or error handling mechanism
    }
    }

}
