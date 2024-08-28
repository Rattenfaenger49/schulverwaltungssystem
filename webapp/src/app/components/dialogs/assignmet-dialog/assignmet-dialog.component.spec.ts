import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignmetDialogComponent } from './assignmet-dialog.component';

describe('AssignmetDialogComponent', () => {
  let component: AssignmetDialogComponent;
  let fixture: ComponentFixture<AssignmetDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [AssignmetDialogComponent]
});
    fixture = TestBed.createComponent(AssignmetDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
