import { isLoggedIn} from "../../guards/auth.guard";

import {Routes} from "@angular/router";
import {TeachersListComponent} from "./teachers-list/teachers-list.component";
import {TeacherComponent} from "./teacher/teacher.component";
import {TeacherResolver} from "../../_services/resolver/teacher.resolver";
import {Role} from "../../types/enums/role";

export const TeachersModuls: Routes = [

    {
        path: '',
        component: TeachersListComponent,
        canActivate: [isLoggedIn],
        data: {role: [Role.ADMIN]}
    },
    {
        path: ':id',
        component: TeacherComponent,
        canActivate: [isLoggedIn],
        data: {role: [Role.ADMIN]},
        resolve: {
            user: TeacherResolver
        }
    },
];
