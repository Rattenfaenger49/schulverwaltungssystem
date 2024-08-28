import {Component, input, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {Lesson} from "../../../types/lesson";
import {Page} from "../../../types/page";
import {LessonService} from "../../../_services/data/lesson.service";
import {AsyncPipe, JsonPipe, NgClass, NgOptimizedImage} from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import {SearchInputComponent} from "../../shared/search-input/search-input.component";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../../../_services/data/auth.service";
import {Person} from "../../../types/person";
import {CustomPageableTableComponent} from "../../shared/custom-pageable-table/custom-pageable-table.component";
import {CustomPageableTable} from "../../shared/custom-pageable-table/interfaces/custom-pageable-table";
import {LoaderComponent} from "../../shared/loader/loader.component";
import {PageableCardsListComponent} from "../../shared/pageable-cards-list/pageable-cards-list.component";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {getKeys, getSortableKeys} from "../../utils";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {CustomPageableCard} from "../../shared/custom-pageable-table/interfaces/custom-pageable-card";
import {ViewSwitcherComponent} from "../../shared/view-switcher/view-switcher.component";

// TODO implement a filter for single and group lessons
// TODO add to the filter with lessons without lessons


@Component({
  selector: "ys-lessons-list",
  templateUrl: "./lessons-list.component.html",
  styleUrls: ["./lessons-list.component.scss"],
  standalone: true,
  imports: [
    RouterLink,
    MatIconModule,
    CustomPageableTableComponent,
    AsyncPipe,
    SearchInputComponent,
    JsonPipe,
    FormsModule,
    NgClass,
    LoaderComponent,
    PageableCardsListComponent,
    MatOption,
    MatSelect,
    MatButtonToggle,
    MatButtonToggleGroup,
    NgOptimizedImage,
    ViewSwitcherComponent,
  ],
})
export class LessonsListComponent implements OnInit, CustomPageableTable, CustomPageableCard {
  data = signal<Page<Lesson> |null>(null);
  searchInput = signal("");
  currentPage = signal(0);
  user = input<Person>();
  filterType = signal<string>('');
  sorting = signal<{ key: string, sortDirection: 'asc'| 'desc' }>({key: 'id', sortDirection: 'asc'});
  isNotSortable = signal<string[]>(['isSigned', 'students',  'teacher']);
  sortableKeys = signal<{ name:string, translation: string}[]>([]);
  isFirstFetch = true;
  public filteredKeys = ["id", "teacherId"];
  view: 'table' | 'card' = 'table';
  
  sortCriteria: string = 'id';
  sortDirection: "asc" | "desc" = 'asc';
   constructor(
      private lessonService: LessonService,
      private route: ActivatedRoute,
      private router: Router,
      public auth: AuthService,
  ) {
    if (this.auth.isStudent()) {
      this.filteredKeys.push("isSigned");
    }
    this.route.queryParams.subscribe(p => {
      this.filterType.set(p['filter'] ?? '') ;
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

    this.lessonService.getLessons(
        {query: this.searchInput(), userId: this.user()?.id ?? '', param:this.filterType()},
        {key: this.sortCriteria, sortDirection: this.sortDirection},
        this.currentPage(),
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
    });;
  }
  
  onRowClicked(element: any): void {
    this.router.navigate(['lessons', element.id]);
  }
  
  onTableSortChange(sort: { key: string; sortDirection: "asc" | "desc" }): void {
    this.sortCriteria = sort.key;
    this.sortDirection = sort.sortDirection;
    this.load()
  }
  
  onCardSortChange(): void {
    // TODO find a way to solve this in a prettier way
    setTimeout( () => this.load(),10);
    
  }
}

