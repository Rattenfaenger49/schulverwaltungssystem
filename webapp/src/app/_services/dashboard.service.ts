import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SystemCPU } from '../types/system-CPU';
import { SystemlHealth } from '../types/system-health';
import {environment} from "../../environments/environment";
const API_URL = environment.apiUrl + '/actuator';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  constructor(private http:HttpClient) { }
  getHttpTraces():Observable<any>{
    return this.http.get<any>(`${API_URL}/httpexchanges`);
  }

  getSystemHealth():Observable<SystemlHealth>{
    return this.http.get<SystemlHealth>(`${API_URL}/health`);
  }

  getSystemCPU():Observable<SystemCPU>{
    return this.http.get<SystemCPU>(`${API_URL}/metrics/system.cpu.count`);
  }

  getProcessUptime():Observable<any>{
    return this.http.get<any>(`${API_URL}/metrics/process.uptime`);
  } 
}
