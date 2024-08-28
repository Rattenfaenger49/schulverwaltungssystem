import {ChangeDetectionStrategy, Component, EventEmitter, input, Output, signal,} from '@angular/core';
import {Page} from "../../../types/page";
import {RenderContentPipe} from '../../../_services/pipes/RenderContentPipe';
import {NgxPaginationModule} from 'ngx-pagination';
import {RouterLink} from '@angular/router';
import {MatIconModule} from "@angular/material/icon";
import {SearchInputComponent} from '../search-input/search-input.component';
import {MatListItem} from "@angular/material/list";
import {MatPaginator} from "@angular/material/paginator";
import {
	MatCell,
	MatCellDef,
	MatHeaderCell,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from "@angular/material/table";
import {DatePipe} from "@angular/common";
import {translations} from "../../../translations/translations";

@Component({
	selector: "ys-custom-pageable-table",
	templateUrl: "./custom-pageable-table.component.html",
	styleUrls: ["./custom-pageable-table.component.css"],
	changeDetection: ChangeDetectionStrategy.OnPush,
	standalone: true,
	imports: [
		SearchInputComponent,
		RouterLink,
		NgxPaginationModule,
		RenderContentPipe,
		MatListItem,
		MatIconModule,
		MatPaginator,
		MatHeaderRow,
		MatCell,
		MatHeaderCell,
		MatTable,
		MatRow,
		MatCellDef,
		MatHeaderRowDef,
		MatRowDef,
		DatePipe
	],
})
export class CustomPageableTableComponent {
	data = input.required<Page<any>>();
	baseUrl = input<string>('');
	filter = input<string[]>([]);
	isNotSortable = input<string[]>([]);
	sorting = input<{ key: string, sortDirection: 'asc' | 'desc' }>({key: 'id', sortDirection: 'asc'});
	@Output() pageNumberChange: EventEmitter<number> = new EventEmitter<number>();
	@Output() sortChange: EventEmitter<{ key: string, sortDirection: 'asc' | 'desc' }> = new EventEmitter<{
		key: string,
		sortDirection: 'asc' | 'desc'
	}>();
	@Output() rowClicked: EventEmitter<any> = new EventEmitter<any>();
	protected currentPage = signal<number>(0);
	
	
	constructor() {
	}
	
	getHeaders(): { name: string, translation: string }[] {
		return Object.keys(this.data()?.content[0] ?? {}).filter(
			(header) => !this.filter().includes(header)
		).map(header => ({name: header, translation: this.translate(header)}));
	}
	
	onSortChange(headerKey: string){
		
		if (this.sorting().key === headerKey) {
			this.sorting().sortDirection = this.sorting().sortDirection === 'asc' ? 'desc' : 'asc';
		} else {
			this.sorting().key = headerKey;
			this.sorting().sortDirection = 'asc';
		}
		this.sortChange.emit(this.sorting());
		
	}
	
	
	emitPageNumber() {
		this.pageNumberChange.emit(this.currentPage());
	}
	
	changePage(page: number) {
		this.currentPage.set(page);
		this.emitPageNumber();
	}
	
	protected readonly Array = Array;
	
	private translate(headerName: string) {
		// Translate each header name using the translations object
		return translations[headerName] || headerName;
	}
	
	splitContent(content: string) {
		return content.split(";");
	}
	

	
	onRowClicked(content: any) {
		this.rowClicked.emit(content);
		
	}
	
	
}
