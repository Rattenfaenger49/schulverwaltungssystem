import {inject, Injectable} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class UrlParamServiceService {

  route = inject(ActivatedRoute);
  router = inject(Router);
  
  public onTabChange(key:string,tab: number) {
    const parameters = { ...this.route.snapshot.queryParams };
    parameters['n-tab'] = null;
    const queryParams = { ...parameters, [key]: tab };
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: queryParams,
      queryParamsHandling: 'merge', // Merge with existing query parameters
    });
  }
}
