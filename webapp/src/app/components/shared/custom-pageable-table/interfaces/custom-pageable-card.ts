import {WritableSignal} from "@angular/core";

export interface CustomPageableCard {
	sortCriteria: string ;
	sortDirection: 'asc'| 'desc' ;
	sortableKeys: WritableSignal<{ name:string, translation: string}[]>;
	onCardSortChange():  void;
	
}
