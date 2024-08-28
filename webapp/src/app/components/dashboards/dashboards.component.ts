import {Component} from '@angular/core';
import {DProgressBar} from "../../_services/directivs/DProgressBar";
import {DecimalPipe} from "@angular/common";

import {AdminDashboardComponent} from "./admin-dashboard/admin-dashboard.component";
import {TeacherComponent} from "../teachers/teacher/teacher.component";
import {TeacherDashboardComponent} from "./teacher-dashboard/teacher-dashboard.component";
import {StudentDashboardComponent} from "./student-dashboard/student-dashboard.component";
import {AuthService} from "../../_services/data/auth.service";
import {Role} from "../../types/enums/role";

@Component({
  selector: "ys-dashboards",
  standalone: true,
  imports: [
    DProgressBar,
    DecimalPipe,
    AdminDashboardComponent,
    TeacherComponent,
    TeacherDashboardComponent,
    StudentDashboardComponent,
  ],
  templateUrl: "./dashboards.component.html",
  styleUrl: "./dashboards.component.scss",
})
export class DashboardsComponent {


  constructor(public auth: AuthService) {
  }
	
	protected readonly Role = Role;
}
