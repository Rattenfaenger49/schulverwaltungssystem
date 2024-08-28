import {ChangeDetectionStrategy, Component, EventEmitter, input, Output, signal} from '@angular/core';
import {Page} from "../../../types/page";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitleGroup} from "@angular/material/card";
import {NgxPaginationModule} from "ngx-pagination";
import {DatePipe} from "@angular/common";
import {RenderContentPipe} from "../../../_services/pipes/RenderContentPipe";
import {MatButton} from "@angular/material/button";
import {FormsModule} from "@angular/forms";
import {translateKey, translations} from "../../../translations/translations";
import {SearchInputComponent} from "../search-input/search-input.component";

@Component({
  selector: 'ys-pageable-cards-list',
  standalone: true,
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitleGroup,
    MatCardContent,
    NgxPaginationModule,
    DatePipe,
    RenderContentPipe,
    MatCardActions,
    MatButton,
    FormsModule,
    SearchInputComponent
  ],
  templateUrl: './pageable-cards-list.component.html',
  styleUrl: './pageable-cards-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageableCardsListComponent {
  sortCriteria: string = 'name'; // Default sorting criteria
  sortDirection: 'asc' | 'desc' = 'asc'; // Default sorting direction
  data = input.required<Page<any>>();
  baseUrl = input<string>('');
  filter = input<string[]>([]);
  @Output() pageNumberChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() rowClicked: EventEmitter<any> = new EventEmitter<any>();
  protected currentPage = signal<number>(0);
  
  constructor() {
  }
  
  getKeys(): { name: string, translation: string }[] {
    return Object.keys(this.data()?.content[0] ?? {}).filter(
        (header) => !this.filter().includes(header)
    ).map(header => ({name: header, translation: translateKey(header)}));
  }
  
  emitPageNumber() {
    this.pageNumberChange.emit(this.currentPage());
  }
  
  changePage(page: number) {
    this.currentPage.set(page);
    this.emitPageNumber();
  }
  
  filterType: any;
  
  private translate(headerName: string) {
    // Translate each header name using the translations object
    return translations[headerName] || headerName;
  }
  
  splitContent(content: string) {
    return content.split(";");
  }
  
  
  onCardClicked(content: any) {
    this.rowClicked.emit(content);
    
  }
  
  
  
}
