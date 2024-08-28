import {Component, model} from '@angular/core';
import {NgClass, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'ys-view-switcher',
  standalone: true,
	imports: [
		NgClass,
		NgOptimizedImage
	],
  templateUrl: './view-switcher.component.html',
  styleUrl: './view-switcher.component.scss'
})
export class ViewSwitcherComponent {
	
	 view = model<string>('table');
	
	setView(view: string) {
		this.view.set(view);
	}
	
}
