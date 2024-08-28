
import {Routes} from "@angular/router";
import {isLoggedIn,} from "../../guards/auth.guard";
import {ContractsListComponent} from "./contracts-list/contracts-list.component";
import {ContractComponent} from "./contract/contract.component";
import {ContractResolver} from "../../_services/resolver/contract.resolver";


export const ContractsModuls: Routes = [

    {
        path: '',
        component: ContractsListComponent,
        canActivate: [isLoggedIn],
       
    },
    {
        path: 'create',
        component: ContractComponent,
        canActivate: [isLoggedIn],

    },
    {
        path: ':id',
        component: ContractComponent,
        canActivate: [isLoggedIn],

        resolve: {
            contract: ContractResolver
        }
    },
];
