import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ResponseObject} from "../../types/ResponseObject";
import {Teacher} from "../../types/teacher";
import {Page} from "../../types/page";
import {EMPTY, Observable} from "rxjs";
import {TeacherService} from "../data/teacher.service";
import {catchError} from "rxjs/operators";
import {Injectable} from "@angular/core";

@Injectable()
export class TeachersResolver implements Resolve<ResponseObject<Page<Teacher>>> {

    constructor(
        private teacherService: TeacherService,
        private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Page<Teacher>>>  {
        return this.teacherService.getTeachers().pipe(
            catchError((err) => {
                console.error(err)
                this.router.navigate(['/error']);
                return EMPTY;
            })
        );


    }

}
