import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {Student} from "../../../types/student";
import {Page} from "../../../types/page";
import {StudentService} from "../../../_services/data/student.service";
import {AsyncPipe, NgClass, NgOptimizedImage} from '@angular/common';
import {SearchInputComponent} from "../../shared/search-input/search-input.component";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "../../../_services/data/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {CustomPageableTableComponent} from "../../shared/custom-pageable-table/custom-pageable-table.component";
import {
  CustomPageableTable
} from "../../shared/custom-pageable-table/interfaces/custom-pageable-table";
import {ResponseService} from "../../../_services/ResponseService";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {PageableCardsListComponent} from "../../shared/pageable-cards-list/pageable-cards-list.component";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {CustomPageableCard} from "../../shared/custom-pageable-table/interfaces/custom-pageable-card";
import {getKeys, getSortableKeys} from "../../utils";
import {ViewSwitcherComponent} from "../../shared/view-switcher/view-switcher.component";




@Component({
  selector: "ys-students-list",
  templateUrl: "./students-list.component.html",
  styleUrls: ["./students-list.component.scss"],
  standalone: true,
	imports: [
		CustomPageableTableComponent,
		AsyncPipe,
		SearchInputComponent,
		FormErrorDirective,
		ReactiveFormsModule,
		FormsModule,
		NgClass,
		MatOption,
		MatSelect,
		PageableCardsListComponent,
		MatButtonToggleGroup,
		MatButtonToggle,
		NgOptimizedImage,
		ViewSwitcherComponent,
	],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StudentsListComponent implements OnInit, CustomPageableTable, CustomPageableCard {
  data = signal<Page<Student> | null>(null);
  searchInput = signal("");
  currentPage = signal(0);
  sorting = signal<{ key: string, sortDirection: 'asc'| 'desc' }>({key: 'id', sortDirection: 'asc'});
  isNotSortable = signal<string[]>([]);
  sortableKeys = signal<{ name:string, translation: string}[]>([]);
  
  filteredKeys: string[] = [
    "id",
    "address",
    "contracts",
    "verified",
    "comment",
    "level",
    "parent",
    "markedForDeletion",
    "teachers"
  ];
  filterType = "";
  responseService = inject(ResponseService);
  view: 'table' | 'card' = 'table';
  sortCriteria: string = 'id';
  sortDirection: 'asc'| 'desc' = 'asc';
  isFirstFetch = true;
  
  
  constructor(
    private studentService: StudentService,
    public auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.route.queryParams.subscribe(p =>{
      this.filterType = p['filter'] ?? '';
    });
    this.load();
    
    
  }
  ngOnInit(): void {
  

  }
  load() {

    this.studentService.getSutdents(
        this.searchInput() ,this.filterType,
        this.currentPage(), {key: this.sortCriteria, sortDirection: this.sortDirection})
        .subscribe({
          next: res => {
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

  changePage(page: number) {
    this.currentPage.set(page);
    this.load();
  }
  changeSearch(text: string) {
    this.searchInput.set(text);
    this.currentPage.set(0);
    this.load();
  }
  
  onRowClicked(element: any): void {
    this.router.navigate(['students', element.id]);
    
  }
  filterChange() {
    this.currentPage.set(0);
    this.updateQueryParams({filter : this.filterType})
    this.load();
  }
  
  updateQueryParams(newParams: any) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: newParams,
      queryParamsHandling: 'merge' // Merge with existing query parameters
    });
  }
  onCardSortChange(): void {
    // TODO find a way to solve this in a prettier way
    setTimeout( () => this.load(),10);

  }
  onTableSortChange(sort: { key: string, sortDirection: 'asc'| 'desc' }): void {
    this.sortCriteria= sort.key;
    this.sortDirection = sort.sortDirection;
    this.load();
  }
  
  
  protected readonly getKeys = getKeys;
}
