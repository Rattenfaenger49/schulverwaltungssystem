import {WritableSignal} from "@angular/core";

export interface CustomListTable {
    changeSearch(text: string): void;
    onRowClicked(element: any): void;
    searchInput:  WritableSignal<string>;

}
