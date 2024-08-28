import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomPageableTableComponent } from './custom-pageable-table.component';

describe('CustomPageableTableComponent', () => {
  let component: CustomPageableTableComponent;
  let fixture: ComponentFixture<CustomPageableTableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [CustomPageableTableComponent]
});
    fixture = TestBed.createComponent(CustomPageableTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
