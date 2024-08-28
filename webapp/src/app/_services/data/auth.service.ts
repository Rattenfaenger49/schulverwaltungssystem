import {computed, Injectable, signal} from '@angular/core';
import { Observable} from 'rxjs';
import {StorageService} from "../storage.service";
import {ResponseObject} from "../../types/ResponseObject";
import {HttpClientService} from "../http-client-service";
import {Claims} from "../../types/claims";
import {Router} from "@angular/router";


@Injectable({
  providedIn: "root",
})
export class AuthService {
  isLoggedIn = signal<boolean>(false);
  isLoggedOut = computed(() => !this.isLoggedIn());
  userRole = signal<string>('');


  public fullName = signal<string>("");
  public schoolId = signal<string|null>("");
  public at = signal<string | null>("");
  public rt = signal<string | null>("");
  public claims = signal<Claims | null>(null);

  constructor(
    private http: HttpClientService,
    private storage: StorageService,
    private router: Router,
  ) {


  }
  async init() {
    const credentials = await this.storage.getCredentials();
    const schoolId = await this.storage.get('tenantId');
    this.schoolId.set(schoolId);
    if (!credentials) {
      this.clearUserState();
      return Promise.resolve();
    }
    this.at.set(credentials.accessToken);
    this.rt.set(credentials.refreshToken);

    this.claims.set(this.extractUserFromToken(this.at()));

    if (this.isTokenValid(this.at())) {
      
      this.updateState();
      return Promise.resolve();
    }
    if (this.isTokenValid(this.rt())) {
      // TODO  optimize refresh request
      this.refreshToken().subscribe({
        next: (data: any) => {
          this.loginHandler(data);
        },
        error: (err: any) => {
          this.clearUserState();
        },
      });
    } else {

      this.storage.removeCredentials();
      this.at.set(null);
      this.rt.set(null);
      this.fullName.set("");
      this.claims.set(null);
    }
    
  }
  // TODO use the customHttpClient
  login(data: any): Observable<any> {
    return this.http.post(
      '/auth/login',
      {
        username: data.username,
        password: data.password,
      }, true, false
    );
  }

  register(data: any): Observable<any> {
    return this.http.post("/register/" + data["userType"].toLowerCase(), data);
  }

  logout(): Observable<any> {
    return this.http.post("/auth/logout", {});
  }
  
  resendConfirmationEmail(userId: number){
    return this.http.post("/register/resend-confirmation-token-by-user?userId="+userId, {});
  }
  public isAdmin(): boolean{
    return this.userRole().includes('ADMIN');
  }
  public isTeacher(): boolean{
    return this.userRole().includes('TEACHER');
  }
  public isStudent(): boolean{
    return this.userRole().includes('STUDENT');
  }
  public refreshToken() {
    return this.http.post<any>(
        "/auth/refresh",
        {
          refreshToken: this.rt(),
        },
    );
  }
  public hasRole(authorities: string | undefined, role: string): boolean {
    if (!authorities) {
      return false;
    }
    return authorities.indexOf(role) > -1;
  }

  async updateUserState(data: any) {
    this.at.set(data.accessToken);
    this.rt.set(data.refreshToken);
    this.updateState();

  }

  clearUserState() {
    this.isLoggedIn.set(false);
    this.userRole.set('');
    this.fullName.set("");
    this.at.set(null);
    this.rt.set(null);
    this.claims.set(null);
    this.storage.removeCredentials();
  }
  verifyToken(token: string, ): Observable<ResponseObject<string>> {
    return this.http.get<ResponseObject<string>>(
      "/auth/confirm?token=" + token, true);
  }

  setPassword(data: any) {
    return this.http.put<ResponseObject<any>>("/auth/set-password", data);
  }



  /*
    getCsrfToken() {
        return this.http.get<ResponseObject<string>>('/auth/csrf/token');
    }

     */

  sendNewConfirmationToken(token: string) {
    return this.http.post<ResponseObject<string>>(
      "/register/resend-confirmation-token?token=" + token,
      {},
    );
  }

  resetPassword(data: any) {
    return this.http.post<ResponseObject<string>>(
      "/register/reset-password",
      data,
    );
  }

  loginHandler(data: any) {
    this.storage.saveJWT(data);
    this.updateUserState(data);
    this.router.navigateByUrl("/dashboard");
  }

  extractUserFromToken(token: any): any {
    if (token) {
      const jwtToken = token.split(".");
      // Decode and replace for sonder charachter like Ü, ß,Ä, Ö
      const base64Payload = jwtToken[1].replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64Payload).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    }
    return null;
  }

  isTokenValid(token: string | null) {
    try {
      if (!token || token === "") return false;

      const jsonToken = JSON.parse(atob(token.split(".")[1]));

      return jsonToken.exp >= Math.floor(Date.now() / 1000);
    } catch (e) {
      return false;
    }
  }



  private updateState() {
    this.claims.set(this.extractUserFromToken(this.at()));
    this.isLoggedIn.set(true);
    
    this.userRole.set(this.claims()!.roles);
    const _n = `${this.claims()?.firstName} ${this.claims()?.lastName}`;
    this.fullName.set(_n.normalize() ?? "");

  }

   getSchoolId() {
    return  this.schoolId();

  }
  getClaims() {
    return  this.claims();
    
  }


  saveSchoolId(value: string) {
    this.schoolId.set(value);
    this.storage.set("tenantId", value);
    
  }
  
  removeSchoolId() {
    this.storage.remove("tenantId");
    this.schoolId.set(null);
  }
}
