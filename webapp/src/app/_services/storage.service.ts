import {Injectable, signal} from "@angular/core";
import { Storage } from "@ionic/storage-angular";
import {Claims} from "../types/claims";

const USER_KEY = "_uc";
/***
 storage.forEach((key, value, index) => {
 });
 ***/
@Injectable({
  providedIn: "root",
})
export class StorageService{

    constructor(private storage: Storage) {
      this.init();
   }

   async init() {
      await this.storage.create();

   }


  public set(key: string, value: any) {
       this.storage?.set(key, value);
  }
  public async getCredentials() {
      return await this.get(USER_KEY);
  }
  public async get(key: string) {
      const value = await this.storage?.get(key);
      return value;
  }
  public remove(key: string) {
    this.storage?.remove(key);
  }

  /**
   * clean the local storage
   */
  public clearStorage() {
    this.storage?.clear();
  }

  public saveUser(token: any) {

    const claims = this.extractUserFromToken(token.accessToken);
    const jsonClaims = btoa(JSON.stringify(claims));
    this.set(USER_KEY, jsonClaims);
    return JSON.parse(atob(jsonClaims));

  }

  extractUserFromToken(token: any): any {
    if (token) {
      const jwtToken = token.split(".");
      return JSON.parse(atob(jwtToken[1]));
    }
    throw new Error("Token is null");
  }



  private isValid(exp: any) {

    return exp >= Math.floor(Date.now() / 1000);
  }




  saveJWT(data: any) {
    this.set(USER_KEY, data);
  }


  removeCredentials() {
    this.remove(USER_KEY);
  }
}

/**
 *  saveCsrfToken(data: any) {
 *     this.csrfToken = data!.token;
 *
 *   }
 *   getCsrfToken(): string | null {
 *     return this.csrfToken;
 *   }
 *
 *
 *  public getToken(): any {
 *     const user = window.localStorage.getItem(USER_KEY);
 *     if (user) {
 *       const userasJson = JSON.parse(user)
 *       if(userasJson.accessToken)
 *         return userasJson.accessToken;
 *     }
 *
 *     return null;
 *   }
 *
 *
 *
 * getRefreshToken() {
 *     const user = window.localStorage.getItem(USER_KEY);
 *     if (user) {
 *       const userAsJson = JSON.parse(user);
 *       if (userAsJson.accessToken) {
 *         const tokenParts = userAsJson.accessToken.split('.');
 *         const encodedPayload = tokenParts[1];
 *         const decodedToken = atob(encodedPayload);
 *         const obj = JSON.parse(decodedToken);
 *         const authorities = obj['authorities'];
 *         if (authorities) {
 *           return authorities;
 *         }
 *       }
 *     }
 *     return '';
 *   }*/
