import {Component, HostListener, inject, OnInit, signal, ViewChild} from "@angular/core";
import {AuthService} from "./_services/data/auth.service";
import {
	NavigationCancel,
	NavigationEnd,
	NavigationError,
	NavigationStart,
	Router,
	RouterLink,
	RouterOutlet
} from "@angular/router";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatListModule} from "@angular/material/list";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatMenuModule} from "@angular/material/menu";
import {MatTooltipModule} from "@angular/material/tooltip";
import {AsyncPipe, NgIf, NgOptimizedImage} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";
import {HttpClientXsrfModule} from "@angular/common/http";
import {App as CapacitorApp} from '@capacitor/app';
import {LoaderComponent} from "./components/shared/loader/loader.component";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {Role} from "./types/enums/role";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {ResetPasswordComponent} from "./components/authentication/reset-password/reset-password.component";
import {SwUpdate} from "@angular/service-worker";
import {NgxExtendedPdfViewerModule} from "ngx-extended-pdf-viewer";


@Component({
	selector: "ys-root",
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.scss"],
	standalone: true,
	imports: [
		MatToolbarModule,
		MatButtonModule,
		MatIconModule,
		MatTooltipModule,
		RouterLink,
		MatMenuModule,
		MatSidenavModule,
		MatListModule,
		MatExpansionModule,
		RouterOutlet,
		AsyncPipe,
		HttpClientXsrfModule,
		NgOptimizedImage,
		LoaderComponent,
		NgIf,
		MatProgressSpinner,
		TooltipModule,
		ResetPasswordComponent,
		NgxExtendedPdfViewerModule,
	],
	
	
})
export class AppComponent implements OnInit {
	isScreenSmall = signal(false);
	@ViewChild("sidenav") sidenav: any;
	loading = signal(false);
	swUpdate = inject(SwUpdate);
	
	@HostListener("window:resize", ["$event"])
	onResize(event: any): any {
		this.sidenav.close();
	}
	constructor(
		public auth: AuthService,
		private router: Router,
		private breakpointObserver: BreakpointObserver
	) {

		this.breakpointObserver
			.observe([Breakpoints.Medium, Breakpoints.Small, Breakpoints.XSmall])
			.subscribe((result) => {
				this.isScreenSmall.set(result.matches);
			});
		this.router.events.subscribe({
			next: (e: any) => {
				this.navigationInterceptor(e);
				this.closeSidenav();
			},
			error: (e) => this.navigationInterceptor(e),
		});
	}
	isDarkTheme = false;
	toggleTheme() {
		this.isDarkTheme = !this.isDarkTheme;
		if (this.isDarkTheme) {
			document.body.classList.add('dark-theme');
		} else {
			document.body.classList.remove('dark-theme');
		}
	}
	navigationInterceptor(event: any): void {
		if (event instanceof NavigationStart) {
			// console.log("NavigationStart", event);
		}
		if (event instanceof NavigationEnd) {
			// console.log("NavigationEnd", event);
		}
		if (event instanceof NavigationCancel) {
			// console.log("NavigationCancel", event);
		}
		if (event instanceof NavigationError) {
			// console.log("NavigationError", event);
		}
	}
	
	// Method to close the sidenav
	closeSidenav() {
		this.sidenav?.close();
	}
	
	ngOnInit() {
		CapacitorApp.addListener("backButton", ({canGoBack}) => {
			if (!canGoBack) {
				CapacitorApp.exitApp();
			} else {
				window.history.back();
			}
		});
		// check if the Browser dose support sw
		if (this.swUpdate.isEnabled) {
			// SwUpdate is enabled, you can use it here
			
			console.log('Service worker  enabled.');
		} else {
			console.log('Service worker updates are not enabled.');
		}
	}
	
	logout(): void {
		this.auth.logout().subscribe({
			next: () => {
				this.auth.clearUserState();
				this.router.navigateByUrl("/login");
			},
			error: (err) => {
				this.auth.clearUserState();
				this.router
					.navigateByUrl("/login")
					.then((r) => console.error("logout error:", err));
			},
		});
	}
	
	
	refreshPage() {
		window.location.reload();
	}
	
	protected readonly Role = Role;
}
