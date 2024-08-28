
import {Routes} from "@angular/router";
import {LessonsListComponent} from "./lessons-list/lessons-list.component";
import {isLoggedIn} from "../../guards/auth.guard";
import {LessonComponent} from "./lesson/lesson.component";
import {LessonResolver} from "../../_services/resolver/lesson-resolver";
import {Role} from "../../types/enums/role";


export const LessonsModuls: Routes = [

    {
        path: '',
        component: LessonsListComponent,
        canActivate: [isLoggedIn]
    },
    {
        path: 'create',
        component: LessonComponent,
        canActivate: [isLoggedIn],
        data: {role: [Role.TEACHER, Role.ADMIN] },
    },
    {
        path: ':id',
        component: LessonComponent,
        canActivate: [isLoggedIn],
        resolve: {
            lesson: LessonResolver
        }
    },
];
