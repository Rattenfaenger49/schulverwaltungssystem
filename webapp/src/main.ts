import {APP_INITIALIZER, importProvidersFrom, LOCALE_ID, isDevMode} from "@angular/core";
import {AppComponent} from './app/app.component';
import {CdkOverlayOrigin} from '@angular/cdk/overlay';
import {MatDialogModule} from '@angular/material/dialog';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {NgxPaginationModule} from 'ngx-pagination';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppRoutingModule} from './app/app-routing.module';
import {provideAnimations} from '@angular/platform-browser/animations';
import {bootstrapApplication, BrowserModule} from '@angular/platform-browser';
import '@angular/localize/init';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from "@angular/material/core";
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {IonicStorageModule} from "@ionic/storage-angular";
import {httpInterceptorProviders} from "./app/_helpers/http.interceptor";
import {AuthService} from "./app/_services/data/auth.service";
import {CalendarModule, MOMENT} from 'angular-calendar';
import {SchedulerModule} from 'angular-calendar-scheduler';
import moment from 'moment';
import {adapterFactory} from 'angular-calendar/date-adapters/moment';
import {registerLocaleData} from '@angular/common';
import localeDe from '@angular/common/locales/de';
import {NgxMatNativeDateModule} from "@angular-material-components/datetime-picker";
import {provideToastr} from "ngx-toastr";
import { provideServiceWorker } from '@angular/service-worker';
import {environment} from "./environments/environment";

function initializeAuthService(auth: AuthService) {
	return () =>
		auth.init();
	
	
}

registerLocaleData(localeDe);
const dateAdapter = {
	provide: DateAdapter,
	useFactory: adapterFactory
};
const MY_FORMATS = {
	parse: {
		dateInput: 'DD/MM/yyyy',
	},
	display: {
		dateInput: 'DD/MM/yyyy',
		monthYearLabel: 'DD MMM YYYY',
		dateA11yLabel: 'LL',
		monthYearA11yLabel: 'DDDD MMMM YYYY',
	},
};
bootstrapApplication(AppComponent, {
	providers: [
    AuthService,
    {
        provide: APP_INITIALIZER,
        useFactory: initializeAuthService,
        deps: [AuthService],
        multi: true,
    },
    importProvidersFrom(BrowserModule, AppRoutingModule, FormsModule, ReactiveFormsModule, NgxPaginationModule, MatToolbarModule, MatIconModule, MatButtonModule, MatMenuModule, MatTooltipModule, MatSidenavModule, MatListModule, MatExpansionModule, MatInputModule, NgxMatNativeDateModule, MatAutocompleteModule, MatDialogModule, MatDatepickerModule, CdkOverlayOrigin, IonicStorageModule.forRoot(), CalendarModule.forRoot(dateAdapter, {}), SchedulerModule.forRoot({ locale: "de", headerDateFormat: "daysRange" })),
    httpInterceptorProviders,
    provideAnimations(),
    provideToastr(),
    provideHttpClient(withInterceptorsFromDi()),
    {
        provide: DateAdapter,
        useClass: MomentDateAdapter,
        deps: [MAT_DATE_LOCALE],
    },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: LOCALE_ID, useValue: "de-DE" },
    { provide: MOMENT, useValue: moment },
    provideServiceWorker('ngsw-worker.js', {
        enabled: environment.production,
		registrationStrategy: 'registerImmediately'
    })
],
}).catch((err) => console.error(err));

