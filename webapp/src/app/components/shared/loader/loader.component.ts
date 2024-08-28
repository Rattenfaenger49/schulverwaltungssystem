import {Component, effect, inject, Signal} from '@angular/core';
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {LoadingService} from "../../../_services/LoadingService";

@Component({
  selector: 'ys-loader',
  standalone: true,
	imports: [
		MatProgressSpinner
	],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.scss'
})
export class LoaderComponent {
	loading: Signal<boolean>;
	
	loadingService = inject(LoadingService);
	
	constructor() {
		this.loading = this.loadingService.loading;

	}

}
