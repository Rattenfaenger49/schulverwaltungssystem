import {ChangeDetectionStrategy, Component, OnInit, signal, WritableSignal} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {Teacher} from "../../../types/teacher";
import {Page} from "../../../types/page";
import {TeacherService} from "../../../_services/data/teacher.service";
import {AsyncPipe, NgClass, NgOptimizedImage} from "@angular/common";
import {SearchInputComponent} from "../../shared/search-input/search-input.component";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../../../_services/data/auth.service";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {CustomPageableTableComponent} from "../../shared/custom-pageable-table/custom-pageable-table.component";
import {CustomPageableTable} from "../../shared/custom-pageable-table/interfaces/custom-pageable-table";
import {PageableCardsListComponent} from "../../shared/pageable-cards-list/pageable-cards-list.component";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatOption, MatSelect} from "@angular/material/select";
import {CustomPageableCard} from "../../shared/custom-pageable-table/interfaces/custom-pageable-card";
import {translateKey} from "../../../translations/translations";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {getKeys, getSortableKeys} from "../../utils";
import {ViewSwitcherComponent} from "../../shared/view-switcher/view-switcher.component";


@Component({
  selector: "ys-teachers-list",
  templateUrl: "./teachers-list.component.html",
  styleUrls: ["./teachers-list.component.css"],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		CustomPageableTableComponent,
		AsyncPipe,
		SearchInputComponent,
		FormsModule,
		NgClass,
		LoaderComponent,
		PageableCardsListComponent,
		MatSlideToggle,
		MatSelect,
		MatOption,
		MatButtonToggle,
		MatButtonToggleGroup,
		NgOptimizedImage,
		ViewSwitcherComponent,
	],
})
export class TeachersListComponent implements OnInit, CustomPageableTable, CustomPageableCard {
  data = signal<Page<Teacher> |null>( null)
  searchInput = signal("");
  currentPage = signal(0);
  isNotSortable = signal<string[]>([]);
  sorting = signal<{ key: string; sortDirection: "asc" | "desc" }>({key:'id', sortDirection:'asc'});
  sortableKeys = signal<{ name:string, translation: string}[]>([]);
  isFirstFetch = true;
  sortCriteria: string = 'id';
  sortDirection: 'asc' | 'desc' = 'asc';
  
  public filteredKeys = ["id"];
  filterType = '';
  view: 'table' | 'card' = 'table';
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teacherService: TeacherService,
    public auth: AuthService,
  ) {
    this.route.queryParams.subscribe(p =>{
      this.filterType = p['filter'] ?? '';
      this.searchInput.set(p['query'] ?? '');
    });

    
  }

  ngOnInit(): void {
    this.load();
  }

  load() {
   this.teacherService.getTeachers(
      this.searchInput(), this.filterType,
      this.currentPage(),
       {key: this.sortCriteria, sortDirection: this.sortDirection},
    ).subscribe({
      next: (res) =>{
        this.data.set(res.data);
        if(this.isFirstFetch){
          this.isFirstFetch = false;
          this.sortableKeys.set(
              getSortableKeys(
                  getKeys(this.data()?.content[0], this.filteredKeys)
                  , this.isNotSortable()));
        }
      },
      error: err => {
        console.error(err);
        
      }
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
  
  onRowClicked(element: any): void {
    this.router.navigate(['teachers', element.id]);
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
  
  onTableSortChange(sort: { key: string; sortDirection: "asc" | "desc" }): void {
    this.sortCriteria = sort.key;
    this.sortDirection = sort.sortDirection;
    this.load();
  }
  

  onCardSortChange(): void {
    // TODO find a way to solve this in a prettier way
    setTimeout( () => this.load(),10);
    
  }
  
  

}
