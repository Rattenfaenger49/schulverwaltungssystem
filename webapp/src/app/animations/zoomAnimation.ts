import {animate, style, transition, trigger} from '@angular/animations';

export const zoomAnimation = trigger('zoom', [
	transition(':enter', [
		style({transform: 'scale(0.5)', opacity: 0}),
		animate('400ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({transform: 'scale(1)', opacity: 1}))
	]),
	transition(':leave', [
		style({transform: 'scale(1)', opacity: 1}),
		animate('400ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({transform: 'scale(0.5)', opacity: 0}))
	])
]);
