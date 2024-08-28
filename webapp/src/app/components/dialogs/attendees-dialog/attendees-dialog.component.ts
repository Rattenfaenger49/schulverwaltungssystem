import {Component, inject, Inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose, MatDialogConfig,
  MatDialogContent,
  MatDialogRef
} from "@angular/material/dialog";
import { ReactiveFormsModule} from "@angular/forms";
import {StudentService} from "../../../_services/data/student.service";
import {AutocompleteListComponent} from "../../shared/autocomplete-list/autocomplete-list.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatList, MatListItem} from "@angular/material/list";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {AsyncPipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {AppointmentService} from "../../../_services/data/appointment.service";
import {MatInput} from "@angular/material/input";
import {MatDatepickerToggle} from "@angular/material/datepicker";
import {NgxMatDatetimePickerModule} from "@angular-material-components/datetime-picker";
import {Person} from "../../../types/person";
import {Appointment} from "../../../types/Appointment";
import {ToastrService} from "../../../_services/ToastrService";


@Component({
  selector: 'ys-attendees-dialog',
  standalone: true,
  imports: [
    AutocompleteListComponent,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    ReactiveFormsModule,
    MatListItem,
    MatList,
    MatDivider,
    MatFormField,
    AsyncPipe,
    MatIcon,
    MatInput,
    MatLabel,
    MatDatepickerToggle,
    MatSuffix,
    NgxMatDatetimePickerModule,
    MatIconButton,
  ],
  templateUrl: './attendees-dialog.component.html',
  styleUrl: './attendees-dialog.component.scss'
})
export class AttendeesDialogComponent implements  OnInit {



  appointment!: Appointment;
  students: Person[] =[];
  newAttedndees = new Set<Person>();
  toastr = inject(ToastrService);
  constructor(
      private studentService: StudentService,
      private appointmentService: AppointmentService,
      @Inject(MAT_DIALOG_DATA) public data: any,
      private dialogRef: MatDialogRef<AttendeesDialogComponent>,
  ) {
    this.appointmentService.getAppointmentById(data.id, this.data.userId).subscribe({
      next: res => {
        this.appointment = res.data;
        this.fetchStudentsFullName();
        
      },
      error: (err) =>{
        console.error(err);
      }
    });

   
  }
  fetchStudentsFullName() {
    this.studentService.getStudentsFullname().subscribe({
      next: (res) => {
        this.students = res.data;
      },
    });
  }
  ngOnInit(): void {

  }

  onClose(): void {
    this.dialogRef.close(false);
  }
  
  onSave(){
    this.newAttedndees.forEach( a => {
      this.presistAttendee(a);
    })
    
    
    this.dialogRef.close(true);
  }
  addAttendees(attendee: Person) {
    const isAlreadyAdded  = this.appointment.attendees.some((a: Person) => attendee.id === a.id);
    if (isAlreadyAdded)
      return;
    this.appointment.attendees.push(attendee);
    this.newAttedndees.add(attendee);
    
    
  }
  removeAttendees(id: number) {
    this.appointmentService.removeAttendee(this.data.id, id,this.data.userId).subscribe({
      next: res => {
        this.appointment.attendees = this.appointment.attendees.filter(attendee => attendee.id !== id);

      },
      error: (err) =>{
        console.error(err);
      }
    });

  }
  
  private presistAttendee(a: Person) {
    this.appointmentService.addAttendee(this.data.id, a,this.data.userId).subscribe({
      next: res => {
        this.toastr.success("Erfolgreih",  a.firstName + ' ' + a.lastName + ' wurde zum Termin erfolgreich hinzugefügt!');
      },
      error: (err) =>{
        // will casue an error and show a dialog with the error
        // this.toastr.error("Fehler",  a.firstName + ' ' + a.lastName + ' konnte zum Termin nicht hinzugefügt werden!');
        console.error(err);
      }
    });
  }
}
