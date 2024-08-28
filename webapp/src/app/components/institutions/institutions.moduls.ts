
import {Routes} from "@angular/router";
import {InstitutionListComponent} from "./institutions-list/institutions-list.component";
import {CreateInstitutionComponent} from "./create-institution/create-institution.component";
import {InstitutionComponent} from "./institution/institution.component";
import {InstitutionResolver} from "../../_services/resolver/institution.resolver";


export const InstitutionsModuls: Routes = [

    {
        path: '',
        component: InstitutionListComponent,
    },
    {
        path: 'create',
        component: CreateInstitutionComponent,

    },
    {
        path: ':id',
        component: InstitutionComponent,

        resolve: {
            institution: InstitutionResolver
        }
    },
];
