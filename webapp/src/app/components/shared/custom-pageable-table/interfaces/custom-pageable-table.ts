import {WritableSignal} from "@angular/core";

export interface CustomPageableTable {
    changePage(page: number): void;
    changeSearch(text: string): void;
    onRowClicked(element: any): void;
    onTableSortChange(sort: { key: string, sortDirection: 'asc'| 'desc' }): void;
    isNotSortable: WritableSignal< string[]>;
    filteredKeys: string[];
    currentPage: WritableSignal<number>;
    searchInput:  WritableSignal<string>;
    sorting: WritableSignal<{ key: string, sortDirection: 'asc'| 'desc' }>;
}
