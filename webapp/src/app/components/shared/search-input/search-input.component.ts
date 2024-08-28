import {
    AfterViewInit, ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter, Input,
    Output,
    ViewChild
} from '@angular/core';
import {debounceTime, distinctUntilChanged, fromEvent} from "rxjs";
import {map} from "rxjs/operators";
import { MatIconModule } from '@angular/material/icon';
import {CollapseModule} from "ngx-bootstrap/collapse";

@Component({
    selector: 'ys-search-input',
    templateUrl: './search-input.component.html',
    styleUrls: ['./search-input.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [MatIconModule, CollapseModule]
})
export class SearchInputComponent implements  AfterViewInit{
  @Input() inputText: string = '';
  @Output() searchChange = new EventEmitter<string>();
  @ViewChild('searchInput') searchInput!: ElementRef;
    isCollapsed = true;
    message: string = 'expanded';
    
    collapsed(): void {
        this.message = 'collapsed';
    }
    
    collapses(): void {
        this.message = 'collapses';
    }
    
    expanded(): void {
        this.message = 'expanded';
    }
    
    expands(): void {
        this.message = 'expands';
    }
  ngAfterViewInit() {
    fromEvent<any>(this.searchInput.nativeElement, 'input')
        .pipe(
            map(event => event.target.value),
            debounceTime(300),
            distinctUntilChanged()
        )
        .subscribe((search) => {
          this.searchChange.emit(search);
        });
  }

}
