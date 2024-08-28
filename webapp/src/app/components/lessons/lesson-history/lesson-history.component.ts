import {Component, input, signal} from '@angular/core';
import {
	MatAccordion,
	MatExpansionModule,
	MatExpansionPanel,
	MatExpansionPanelDescription,
	MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {LessonService} from "../../../_services/data/lesson.service";
import {LessonVersion} from "../../../types/lesson";
import {DatePipe} from "@angular/common";

@Component({
	selector: 'ys-lesson-history',
	standalone: true,
	imports: [
		MatAccordion,
		MatButton,
		MatExpansionPanel,
		MatExpansionPanelTitle,
		MatExpansionPanelDescription,
		MatIcon,
		MatLabel,
		MatFormField,
		MatInput,
		MatExpansionModule,
		DatePipe,
		RouterLink
	
	],
	templateUrl: './lesson-history.component.html',
	styleUrl: './lesson-history.component.scss'
})
export class LessonHistoryComponent {
	lessonVersions = signal<LessonVersion[]>([]);
	lessonId = input.required<number>();
	
	constructor(
		private route: ActivatedRoute,
		private lessonService: LessonService // Assumed you have a service to fetch lesson data
	) {
	}
	
	ngOnInit(): void {

			this.fetchLessonVersions();

	}
	
	fetchLessonVersions(): void {
		this.lessonService.getLessonVersions(this.lessonId()).subscribe({
				next: (res) => {
					this.lessonVersions.set(res.data);
					
				}
			}
		);
	}
}
