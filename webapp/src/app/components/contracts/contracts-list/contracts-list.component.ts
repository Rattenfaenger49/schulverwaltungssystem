import {ChangeDetectionStrategy, Component, OnInit, signal} from '@angular/core';
import {Page} from "../../../types/page";
import {Contract} from "../../../types/contract";
import {ContractService} from "../../../_services/data/contract.service";
import {AsyncPipe, NgClass, NgOptimizedImage} from '@angular/common';
import {SearchInputComponent} from "../../shared/search-input/search-input.component";
import {MatIcon} from "@angular/material/icon";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "../../../_services/data/auth.service";
import {BsDatepickerModule} from "ngx-bootstrap/datepicker";
import {MatFormField} from "@angular/material/form-field";
import {MatDatepickerToggle, MatDateRangeInput, MatDateRangePicker} from "@angular/material/datepicker";
import {CustomPageableTableComponent} from "../../shared/custom-pageable-table/custom-pageable-table.component";
import {CustomPageableTable} from "../../shared/custom-pageable-table/interfaces/custom-pageable-table";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {PageableCardsListComponent} from "../../shared/pageable-cards-list/pageable-cards-list.component";
import {ContractStatus, ContractStatusList} from "../../../types/enums/ContractStatus";
import {ContractType, ContractTypeList} from "../../../types/enums/ContractType";
import {CustomPageableCard} from "../../shared/custom-pageable-table/interfaces/custom-pageable-card";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {getKeys, getSortableKeys} from "../../utils";
import {ViewSwitcherComponent} from "../../shared/view-switcher/view-switcher.component";

@Component({
	selector: "ys-contracts-list",
	templateUrl: "./contracts-list.component.html",
	styleUrls: ["./contracts-list.component.css"],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		CustomPageableTableComponent,
		AsyncPipe,
		SearchInputComponent,
		MatIcon,
		RouterLink,
		ReactiveFormsModule,
		FormsModule,
		NgClass,
		BsDatepickerModule,
		MatFormField,
		MatDateRangeInput,
		MatDateRangePicker,
		MatDatepickerToggle,
		MatOption,
		MatSelect,
		PageableCardsListComponent,
		MatButtonToggle,
		MatButtonToggleGroup,
		NgOptimizedImage,
		ViewSwitcherComponent,
	],
})
export class ContractsListComponent implements OnInit, CustomPageableTable, CustomPageableCard {
	data = signal<Page<Contract> | null>(null);
	searchInput = signal("");
	currentPage = signal(0);
	sorting = signal<{ key: string, sortDirection: 'asc' | 'desc' }>({key: 'id', sortDirection: 'desc'});
	isNotSortable = signal<string[]>([]);
	sortableKeys = signal<{ name:string, translation: string}[]>([]);
	isFirstFetch = true;
	view: 'table' | 'card' = 'table';
	sortCriteria: string = 'id';
	sortDirection: "asc" | "desc" = 'asc';
	
	filteredKeys = ["id", "moduls", "contact", "comment", "institution", "status"];
	protected readonly ContractStatusList = ContractStatusList;
	protected readonly ContractType = ContractType;
	protected readonly ContractTypeList = ContractTypeList;
	contractType!: ContractType;
	contractStatus!: ContractStatus;
	
	constructor(
		private contractService: ContractService,
		private router: Router,
		private route: ActivatedRoute,
		public auth: AuthService) {
		this.route.queryParams.subscribe(p => {
			
			this.contractStatus = p['contractStatus'] ?? 'ANY';
			this.contractType = p['contractType'] ?? 'ANY';
			this.searchInput.set(p['query'] ?? '');
		});
		
	}
	
	changePage(page: number): void {
		this.currentPage.set(page);
		this.load();
	}
	
	changeSearch(text: string): void {
		this.searchInput.set(text);
		this.currentPage.set(0);
		this.load();
	}
	
	ngOnInit(): void {
		this.load();
	}
	
	load() {
		
		this.contractService.getContracts(
			this.searchInput(),
			this.contractType,
			this.contractStatus,
			{key: this.sortCriteria, sortDirection: this.sortDirection},
			this.currentPage(),
		).subscribe({
			next: (res) => {
				this.data.set(res.data);
				if(this.isFirstFetch){
					this.isFirstFetch = false;
					this.sortableKeys.set(
						getSortableKeys(
							getKeys(this.data()?.content[0], this.filteredKeys)
							, this.isNotSortable()));
				}
			}
		});
	}
	
	onRowClicked(element: Contract): void {
		this.router.navigateByUrl(`students/${element.student.id}?tab=1`);
	}
	
	filterChange() {
		this.currentPage.set(0);
		this.updateQueryParams({
			contractType: this.contractType,
			contractStatus: this.contractStatus,
		})
		this.load();
	}
	
	updateQueryParams(newParams: any) {
		this.router.navigate([], {
			relativeTo: this.route,
			queryParams: newParams,
			queryParamsHandling: 'merge' // Merge with existing query parameters
		});
	}
	
	onTableSortChange(sort: { key: string; sortDirection: "asc" | "desc" }): void {
		this.sortCriteria = sort.key;
		this.sortDirection = sort.sortDirection;
		
		this.load();
	}
	
	
	onCardSortChange(): void {
		setTimeout(() => this.load(), 10);
	}
	
	

}
