import {Injectable} from "@angular/core";
import {Teacher} from "../../types/teacher";
import {ResponseObject} from "../../types/ResponseObject";
import {Observable} from "rxjs";
import {HttpClientService} from "../http-client-service";
import {Appointment} from "../../types/Appointment";
import {Person} from "../../types/person";


@Injectable({
    providedIn: 'root',
})
export class AppointmentService{

    private controller = '/appointments';
    constructor(private http: HttpClientService) {
    }

    addAppointment(data: any, userId: number | null): Observable<ResponseObject<Appointment>> {
        if (userId == null)
            return this.http.post<ResponseObject<Appointment>>(this.controller, data);
        else
            return this.http.post<ResponseObject<Appointment>>(`${this.controller}?userId=${userId}`, data);
        

    }
    getAppointments(date:any, userId: number | null): Observable<ResponseObject<Appointment[]>>{
        if (userId == null)
            return this.http.get<ResponseObject<Appointment[]>>(this.controller + "?date=" + date);
        else
            return this.http.get<ResponseObject<Appointment[]>>(`${this.controller}?date=${date}&userId=${userId}`);
        
    }

    getAppointmentById(id: number, userId: number | null): Observable<ResponseObject<Appointment>>{
        if (userId == null)
            return this.http.get<ResponseObject<Appointment>>(this.controller +`/${id}`);
        else
            return this.http.get<ResponseObject<Appointment>>(`${this.controller}/${id}?userId=${userId}`);
        
    }



    updateAppointment(data: any, userId: number | null): Observable<ResponseObject<Appointment>> {
        if (userId == null)
            return this.http.put<ResponseObject<Appointment>>(this.controller+`/${data.id}`, data);
        else
            return this.http.put<ResponseObject<Appointment>>(this.controller+`/${data.id}?userId=${userId}`, data);
        
    }



    deleteAppointment(id: string) {
        return this.http.delete<ResponseObject<Teacher>>(this.controller + `/${id}`);

    }


    getAttendees(id: number, userId: number | null) {
        if (userId == null)
            return this.http.get<ResponseObject<Person>>(this.controller + `/${id}/attendees`)
        else
            return this.http.get<ResponseObject<Person>>(this.controller + `/${id}/attendees?userId=${userId}`)
    }

    addAttendee(id:string ,attendee: Person, userId: number | null) {
        if (userId == null)
            return this.http.post<ResponseObject<Person>>(this.controller + `/${id}/attendees`, {id: attendee.id})
        else
            return this.http.post<ResponseObject<Person>>(this.controller + `/${id}/attendees?userId=${userId}`, {id: attendee.id})
    }
    removeAttendee(id:number ,attendee: number, userId: number | null) {
        if (userId == null)
            return this.http.delete<ResponseObject<Person>>(this.controller + `/${id}/attendees/${attendee}`)
        else
            return this.http.delete<ResponseObject<Person>>(this.controller + `/${id}/attendees/${attendee}`)
    }
}
