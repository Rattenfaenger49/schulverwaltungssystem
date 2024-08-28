import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstitutionsListComponent } from './institutions-list.component';

describe('InstitutionsListComponent', () => {
  let component: InstitutionsListComponent;
  let fixture: ComponentFixture<InstitutionsListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [InstitutionsListComponent]
});
    fixture = TestBed.createComponent(InstitutionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
