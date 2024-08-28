import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutocompleteListComponent } from './autocomplete-list.component';

describe('AutocompleteListComponent', () => {
  let component: AutocompleteListComponent;
  let fixture: ComponentFixture<AutocompleteListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [AutocompleteListComponent]
});
    fixture = TestBed.createComponent(AutocompleteListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
