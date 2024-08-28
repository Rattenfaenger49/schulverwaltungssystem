import {StudentsListComponent} from "./students-list/students-list.component";
import {isLoggedIn} from "../../guards/auth.guard";
import {StudentComponent} from "./student/student.component";
import {StudentResolver} from "../../_services/resolver/student.resolver";
import {Routes} from "@angular/router";

export const StudentsModuls: Routes = [
    
    {
        path: '',
        component: StudentsListComponent,
        canActivate: [isLoggedIn]
    },
    {
        path: ':id',
        component: StudentComponent,
        canActivate: [isLoggedIn],
        resolve: {
            user: StudentResolver
        }
    },
];
