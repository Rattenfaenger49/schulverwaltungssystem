import {ChangeDetectionStrategy, Component, EventEmitter, input, Input, Output, signal} from '@angular/core';
import {Page} from "../../../types/page";
import { RenderContentPipe } from '../../../_services/pipes/RenderContentPipe';
import { NgxPaginationModule } from 'ngx-pagination';
import { RouterLink } from '@angular/router';
import { MatIconModule } from "@angular/material/icon";

import { SearchInputComponent } from '../search-input/search-input.component';
import {MatListItem} from "@angular/material/list";
import {MatPaginator} from "@angular/material/paginator";
import {
  MatCell,
  MatCellDef,
  MatHeaderCell,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable, MatTableDataSource
} from "@angular/material/table";
import {NgForOf} from "@angular/common";
import {translations} from "../../../translations/translations";

@Component({
  selector: "ys-custom-list-table",
  templateUrl: "./custom-list-table.component.html",
  styleUrls: ["./custom-list-table.component.css"],
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
    NgForOf,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef
  ],
})
export class CustomListTableComponent {
  data = input.required<any[]>();
  filter = input<string[]>([]);
  @Output() pageNumberChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() rowClicked: EventEmitter<any> = new EventEmitter<any>();
  protected currentPage = signal<number>(0);
  
  

  getHeaders(): string[] {
    return Object.keys(this.data()[0] ?? {}).filter(
        (header) => !this.filter().includes(header)
        );
  }
  getHeadersTranslated(): string[] {

    return this.translateHeaders(this.getHeaders());
  }

  emitPageNumber() {
    this.pageNumberChange.emit(this.currentPage());
  }

  changePage(page: number) {
    this.currentPage.set( page);
    this.emitPageNumber();
  }

  protected readonly Array = Array;

  private translateHeaders(headerNames: string[]) {
    // Translate each header name using the translations object
    return headerNames.map(header => translations[header] || header);
  }

  splitContent(content: string) {
    return content.split(";");
  }

  onRowClicked(content: any) {
    this.rowClicked.emit(content);
    
  }
  
  getDataSource() {
    if (this.data !== undefined) {
    return new MatTableDataSource(this.data());
  }
    return new MatTableDataSource<any, MatPaginator>();
}
}
