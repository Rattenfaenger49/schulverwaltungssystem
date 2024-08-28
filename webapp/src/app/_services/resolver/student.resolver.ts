import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Student} from "../../types/student";
import {inject, Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {UserService} from "../data/user.service";
import {ResponseObject} from "../../types/ResponseObject";
import {StudentService} from "../data/student.service";
import {AuthService} from "../data/auth.service";

@Injectable()
export  class StudentResolver implements  Resolve<ResponseObject<Student>>
{

    constructor(private studentService: StudentService,
                private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Student>>{

    const id = route.paramMap.get('id');
        if (id) {
        return this.studentService.getStudent(id);
    }else {
        this.router.navigate(['/error']); // Redirect to an error page
        return EMPTY; // or any other default value or error handling mechanism
    }
    }

}
