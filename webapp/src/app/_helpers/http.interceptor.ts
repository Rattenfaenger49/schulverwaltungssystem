import {inject, Injectable} from "@angular/core";
import {
    HTTP_INTERCEPTORS,
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
} from "@angular/common/http";
import {finalize, Observable, switchMap, throwError, timer,} from "rxjs";
import {catchError} from "rxjs/operators";
import {AuthService} from "../_services/data/auth.service";
import {Router} from "@angular/router";
import {LoadingService} from "../_services/LoadingService";
import {SkipLoading} from "../components/shared/loader/skip.loading";
import {ResponseService} from "../_services/ResponseService";
import {ToastrService} from "../_services/ToastrService";
import {SkipDialogResponse} from "../_services/skip-dialog-response";

interface RequestHeaders {
	[key: string]: string;
}

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
	private isRefreshing = false;
	loadingService = inject(LoadingService);
	auth = inject(AuthService);
	router = inject(Router);
	responseService = inject(ResponseService);
	toastr = inject(ToastrService);
	
	intercept(
		req: HttpRequest<any>,
		next: HttpHandler,
	): Observable<HttpEvent<any>> {
		/******
         enable CSRF-Token
         const token = this.csrfTokenExtrator.getToken() as string;

         if (this.httpRequests.includes(req.method) && token != null) {
         req = req.clone({
         headers: req.headers.set("X-XSRF-TOKEN", token),
         withCredentials: true,
         });
         }else {}
		 **** */
		if(!this.auth.getSchoolId())
		{
			this.toastr.error( "", "School-Id ist nicht gültig!");
			return throwError(() => null);
		}
		
		const requestHeaders: RequestHeaders = {
			"X-TenantID": this.auth.getSchoolId() as string,
		};
		
		
		// Check if the user is logged in
		if (this.auth.isLoggedIn() && !req.url.includes("/auth/refresh")) {
			// Get the access token
			const token = this.auth.at();
			if (token) {
				// Add the Authorization header with the Bearer token
				requestHeaders["Authorization"] = `Bearer ${token}`;
			}
		}
		
		req = req.clone({
			withCredentials: true,
			setHeaders: requestHeaders,
		});
		
		
		// Check for a custom attribute to avoid showing loading spinner
		if (!req.context.get(SkipLoading)) {
			this.loadingService.loadingOn();
			
		}
		
		return next.handle(req).pipe(
			finalize(() => {
				this.loadingService.loadingOff();
			}),
			catchError((error) => {
				if (!navigator.onLine) {
					// Handle offline error
					this.responseService.responseDialog({ status: 'Fehlgeschlagen',
						data: null,
						message:'Keine Internetverbindung. Bitte überprüfen Sie Ihre Netzwerkeinstellungen.'}, false);
					return throwError(() => error)
					
					
				} else if (error.status === 0) {
					// Handle server unreachable error
					this.responseService.responseDialog({ status: 'Fehlgeschlagen',
						data: null,
						message:'Der Server ist derzeit nicht erreichbar. Bitte versuchen Sie es später erneut.'}, false);
					return throwError(() => error)
					
				}else if (
					(error instanceof HttpErrorResponse &&
						!req.url.includes("auth/login") &&
						error.status === 401 &&
						error.error.message === 'Die Sitzung ist abgelaufen.')
				) {
					return this.handle401Error(req, next)
				}
				
				if(!req.context.get(SkipDialogResponse)) {
					this.responseService.responseDialog(error.error, false);
				}
				
				console.error('Error', error);
				return throwError(() => error)
			}),
		);
	}
	
	private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
		if (!this.isRefreshing) {
			
			this.isRefreshing = true;
			
			const bool = this.auth.isTokenValid(this.auth.rt());
			if (bool) {
				this.auth.refreshToken().subscribe({
					next: (data: any) => {
						this.isRefreshing = false;
						this.auth.loginHandler(data);
						this.toastr.success( "", "Sitzung wurde aktualisiert");
					},
					error: (err: any) => {
						this.isRefreshing = false;
						this.auth.clearUserState();
						this.toastr.info("", "Sitzung konnte nicht aktualisiert werden");
						
						throw err
					},
				});
			} else {
				this.isRefreshing = false;
				this.auth.clearUserState();
				this.router.navigateByUrl("/login")
			}
		}
		
		return timer(500).pipe(
			switchMap(() => {
				const updatedRequest = request.clone({
					setHeaders: {
						Authorization: `Bearer ${this.auth.at()}`,
					},
				});
				return next.handle(updatedRequest);
			}),
		);
	}
	
	
}

export const httpInterceptorProviders = [
	{provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true},
];
