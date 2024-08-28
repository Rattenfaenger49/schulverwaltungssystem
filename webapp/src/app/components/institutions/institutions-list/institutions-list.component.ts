import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Page} from "../../../types/page";
import { Router, RouterLink } from "@angular/router";
import {Institution} from "../../../types/Institution";
import { Observable} from "rxjs";
import {ResponseObject} from "../../../types/ResponseObject";
import { AsyncPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import {InstitutionService} from "../../../_services/data/institution.service";
import {SearchInputComponent} from "../../shared/search-input/search-input.component";
import {CustomPageableTableComponent} from "../../shared/custom-pageable-table/custom-pageable-table.component";
import {CustomPageableTable} from "../../shared/custom-pageable-table/interfaces/custom-pageable-table";

@Component({
  selector: "ys-institutions-list",
  templateUrl: "./institutions-list.component.html",
  styleUrls: ["./institutions-list.component.css"],
  standalone: true,
  imports: [
    RouterLink,
    MatIconModule,
    CustomPageableTableComponent,
    AsyncPipe,
    SearchInputComponent,
  ],
})
export class InstitutionListComponent implements OnInit, CustomPageableTable {
  data!: Observable<Page<Institution>>;
  searchInput =  signal('');
  sorting = signal<{ key: string, sortDirection: 'asc'| 'desc' }>({key: 'id', sortDirection: 'asc'});
  isNotSortable = signal<string[]>([]);
  sortableKeys = signal<{ name:string, translation: string}[]>([]);
  
  data$!: Observable<ResponseObject<Page<Institution>>>;
  public filteredKeys = ['id', 'address', 'contacts'];

  constructor(
    private router: Router,
    private institutionService: InstitutionService,
  ) {}

  changePage(page: number): void {
    this.currentPage.set(page);
    this.load();
  }
  changeSearch(text: string): void {
    this.searchInput.set(text);
    this.currentPage.set(0);

    this.load();
  }
  currentPage = signal(1);

  ngOnInit(): void {
    this.data$ = this.institutionService.getInstitutions();
  }
  load() {
    this.data$ = this.institutionService
        .getInstitutions(this.searchInput(), this.currentPage());
  }
  
  onRowClicked(element: any): void {
    this.router.navigate(['institutions', element.id]);
  }
  onTableSortChange(sort: { key: string; sortDirection: "asc" | "desc" }): void {
    this.sorting.set(sort);
  }
  
  
}
