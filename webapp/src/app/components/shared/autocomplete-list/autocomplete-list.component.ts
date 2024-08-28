import {Component, EventEmitter, input, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

import {BehaviorSubject, combineLatest, debounceTime, Observable, of, startWith} from "rxjs";
import {map} from "rxjs/operators";
import {MatDialogRef} from "@angular/material/dialog";
import { MatOptionModule } from '@angular/material/core';
import { AsyncPipe } from '@angular/common';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
    selector: 'ys-autocomplete-list',
    templateUrl: './autocomplete-list.component.html',
    styleUrls: ['./autocomplete-list.component.scss'],
    standalone: true,
    imports: [MatFormFieldModule, MatInputModule, MatAutocompleteModule, MatOptionModule, AsyncPipe]
})
export class AutocompleteListComponent implements  OnChanges{

  elements = input.required<any[]>()
  type= input.required<string>();
  @Output() selectedElemnt: EventEmitter<any> = new EventEmitter<any>();
  elementsFilter$: BehaviorSubject<string> = new BehaviorSubject('');
  list$?: Observable<any[]>

  constructor() {
  }
  selectedElement(element: any) {
    this.selectedElemnt.emit(element);
  }
  filterInputs(value: string): void {
    this.elementsFilter$.next(value);
  }
  private createFilteredTeachersObservable(elements: any[]): Observable<any[]> {
    return combineLatest([this.elementsFilter$.pipe(startWith('')),
      of(elements)]).pipe(
        debounceTime(100),
        map(([filter, elementsList]) => {
          if (!filter) {
            return elementsList;
          }
          const filterValue = filter.toLowerCase();

          return elementsList.filter(
              elements =>
                  (elements.firstName +' ' + elements.lastName).toLowerCase().includes(filterValue)
          );
        })
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['elements']) {
      this.list$ = this.createFilteredTeachersObservable(changes['elements'].currentValue)
    }

  }




}
