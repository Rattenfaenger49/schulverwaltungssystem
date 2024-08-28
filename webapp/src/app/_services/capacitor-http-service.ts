import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse, HttpEvent } from "@angular/common/http";
import { from, Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { CapacitorHttp } from "@capacitor/core";
import { StorageService } from "./storage.service";



const API_URL = environment.apiUrl+ '/api/v1';

@Injectable({
    providedIn: 'root',
})
export class HttpClientService{

    constructor(private http: HttpClient, private storage: StorageService) {

    }
    /*
        getCookie(name: string): any {
            const nameLenPlus = (name.length + 1);
            return document.cookie
                .split(';')
                .map(c => c.trim())
                .filter(cookie => {
                    return cookie.substring(0, nameLenPlus) === `${name}=`;
                })
                .map(cookie => {
                    return decodeURIComponent(cookie.substring(nameLenPlus));
                })[0] || null;
        }
        get csrfToken(): string {
            const csrfToken = this.getCookie('XSRF-TOKEN');
            if (csrfToken) {
                return csrfToken;
            }
            return '';
        }
     */
    private async makeHttpRequest<T>(method: string, url: string, data?: any){
        let jwtToken = await this.storage.get('at') ?? '';

        if(jwtToken !== '' && url != '/auth/refresh')
            jwtToken = 'Bearer '+ jwtToken;

        const option = {
            method: method,
            url: API_URL + url,
            headers: {
                'Content-Type': 'application/json',
                'X-TenantID': 'test',
                'Authorization': jwtToken,
            },
            data: data
        };
        return await CapacitorHttp.request(option).catch(
            (error: HttpErrorResponse) => {
                if (error.status === 401) {
                    throw new Error('Token expired');
                } else {
                    throw error;
                }
            }
        );
    }

    private async refreshToken(): Promise<string> {
        let rtPlain = await this.storage.get('rt') ?? '';
        const rt = JSON.parse(atob(rtPlain.split('.')[1]));
        return  rt.exp > Math.floor(Date.now() / 1000) ? rtPlain : ''; // Placeholder, replace with actual refresh token logic
    }

    private async handleExpiredToken<T>(method: string, url: string, data?: any) {
        // Refresh token
        const refreshToken = await this.refreshToken();
        if (!refreshToken) {
            // Handle refresh token failure
            throw new Error('Failed to refresh token');
        }
        const res = await this.makeHttpRequest<T>('post', '/auth/refresh', {"refreshToken": refreshToken});

        this.storage.set("at", res.data.accessToken);
        this.storage.set("rt", res.data.refreshToken);
        this.storage.saveUser(res.data);

        const response =  await this.makeHttpRequest<T>(method, url, data);

        return   response.data as T;
        // Retry original request with new token
    }

    private async handleRequest<T>(method: string, url: string, data?: any): Promise<T>{
        try {
            const response = await this.makeHttpRequest<T>(method, url, data);
            return   response.data as T;
        } catch (error: any) {
            // TODO handle specific errors
            return this.handleExpiredToken<T>(method, url, data );


        }
    }
    get<T>(url: string): Observable<T> {
        return from(this.handleRequest<T>('GET', url));

    }

    post<T>(url: string, data: any, headers?: any): Observable<T> {

        return from(this.handleRequest<T>('POST', url, data));

    }
    put<T>(url: string, data:any): Observable<T>{
        return from(this.handleRequest<T>('PUT', url, data));

    }
    delete<T>(url: string): Observable<T>{
        return from(this.handleRequest<T>('DELETE', url));

    }
    postFile<T>(url: string, data: FormData): Observable<HttpEvent<T>> {

        return this.http.post<T>(API_URL + url, data , {
            responseType: 'json',
            reportProgress: true,
            observe: 'events'
        });

    }
}

