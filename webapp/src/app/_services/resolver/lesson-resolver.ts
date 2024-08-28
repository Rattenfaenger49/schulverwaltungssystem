import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {EMPTY, Observable} from "rxjs";
import {ResponseObject} from "../../types/ResponseObject";
import {Lesson} from "../../types/lesson";
import {LessonService} from "../data/lesson.service";

@Injectable()
export  class LessonResolver implements  Resolve<ResponseObject<Lesson>>
{

    constructor(private lessonService: LessonService,
                private router:Router) {
    }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ResponseObject<Lesson>>{

        const id = Number(route.paramMap.get('id'));

        if (id) {
            return this.lessonService.getLesson(id);
        }else {
            this.router.navigate(['/error']);
            return EMPTY;
        }



    }

}
