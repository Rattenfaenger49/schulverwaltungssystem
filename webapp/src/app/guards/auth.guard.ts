import {CanActivateFn, Router} from '@angular/router';
import { Observable, of} from "rxjs";
import {inject} from "@angular/core";
import {AuthService} from "../_services/data/auth.service";
import {Role} from "../types/enums/role";


// MAYBE use the auth to check permission
export const isLoggedIn: CanActivateFn = (route, state):Observable<boolean> => {
  const router = inject(Router);
  const auth = inject(AuthService);
  
  
  if (auth.isLoggedOut()) {
    router.navigateByUrl('/login');
    return of(false);
  }


  const roles : Role[] = route.data['role'] ?? [];

  if (roles.length > 0 && !auth.userRole() &&!roles.includes(<Role>auth.userRole())) {
          return of(false);
  }
  
  return of(true);
  

};

export const isLoggedOut: CanActivateFn = (
    route,
    state
):Observable<boolean> => {
  const auth = inject(AuthService);

  return of(auth.isLoggedOut());


};


