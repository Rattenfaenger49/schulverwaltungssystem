import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {RegisterComponent} from "./components/register/register.component";
import {LoginComponent} from "./components/authentication/login/login.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {StudentResolver} from "./_services/resolver/student.resolver";
import {InstitutionResolver} from "./_services/resolver/institution.resolver";
import {TeacherResolver} from "./_services/resolver/teacher.resolver";
import {ContractResolver} from "./_services/resolver/contract.resolver";
import {LessonResolver} from "./_services/resolver/lesson-resolver";
import {ConfirmationComponent} from "./components/authentication/confirmation/confirmation.component";
import {UpdatePasswordComponent} from "./components/authentication/update-password/update-password.component";
import {TeachersResolver} from "./_services/resolver/teachers.resolver";
import {isLoggedIn, isLoggedOut} from "./guards/auth.guard";
import {ResetPasswordComponent} from "./components/authentication/reset-password/reset-password.component";
import {DashboardsComponent} from "./components/dashboards/dashboards.component";
import {ProfileResolver} from "./_services/resolver/profile.resolver";
import {CalenderComponent} from "./components/calender/calender.component";
import {MonitoringDashboardComponent} from "./components/monitoring-dashboard/monitoring-dashboard.component";
import {Role} from "./types/enums/role";
import {ClientInfoComponent} from "./components/client-info/client-info.component";
import {ClientInforesolver} from "./_services/resolver/client-info.resolver";

const routes: Routes = [
	

	{
		path: "confirm",
		component: ConfirmationComponent
	},{
		path: "company-info",
		component: ClientInfoComponent,
		canActivate: [isLoggedIn],
		resolve: {
			clientInfo: ClientInforesolver
		},
		data: {
			role: [Role.ADMIN]
		}
	},
	{
		path: "update-password",
		component: UpdatePasswordComponent
	},
	{
		path: "reset-password",
		component: ResetPasswordComponent
	},
	{
		path: "appointments",
		component: CalenderComponent,
		canActivate: [isLoggedIn],
	},
	{
		path: "dashboard",
		component: DashboardsComponent,
		canActivate: [isLoggedIn],
	},
	{
		path: "monitoring-dashboard",
		component: MonitoringDashboardComponent,
		canActivate: [isLoggedIn],
		data: {
			role: [Role.ADMIN]
		}
	},

	{
		path: "students",
		loadChildren: () =>
			import("./components/students/students.moduls").then(
				(m) => m.StudentsModuls,
			),
		canActivate: [isLoggedIn]
	},
	{
		path: "teachers",
		loadChildren: () =>
			import("./components/teachers/teachers.moduls").then(
				(m) => m.TeachersModuls,
			),
		canActivate: [isLoggedIn],
		
	},
	{
		path: "lessons",
		loadChildren: () =>
			import("./components/lessons/lessons.moduls").then(
				(m) => m.LessonsModuls,
			),
		canActivate: [isLoggedIn]
		
	},
	{
		path: "institutions",
		loadChildren: () =>
			import("./components/institutions/institutions.moduls").then(
				(m) => m.InstitutionsModuls,
			),
		canActivate: [isLoggedIn],
		data: {role: [Role.ADMIN]},
	},
	{
		path: "contracts",
		loadChildren: () =>
			import("./components/contracts/contracts.moduls").then(
				(m) => m.ContractsModuls,
			),
		canActivate: [isLoggedIn],
		data: {role: [Role.ADMIN]}
		
	},
	{
		path: "register",
		component: RegisterComponent,
		canActivate: [isLoggedIn],
		data: {
			role: [Role.ADMIN]
		}
	},
	{
		path: "login",
		component: LoginComponent,
		canActivate: [isLoggedOut]
	},
	{
		path: "profile",
		component: ProfileComponent,
		canActivate: [isLoggedIn],
		resolve: {
			user: ProfileResolver
		}
	},
	{path: "error", redirectTo: "home", pathMatch: "full"},
	{path: "**", redirectTo: "dashboard", pathMatch: "full"},
];

@NgModule({
	imports: [RouterModule.forRoot(routes, {enableTracing: false})],
	exports: [RouterModule],
	providers: [
		StudentResolver,
		ProfileResolver,
		InstitutionResolver,
		ContractResolver,
		ClientInforesolver,
		TeacherResolver,
		TeachersResolver,
		LessonResolver],
})
export class AppRoutingModule {
}
