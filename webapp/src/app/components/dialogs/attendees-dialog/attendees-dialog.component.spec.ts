import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendeesDialogComponent } from './attendees-dialog.component';

describe('AttendeesDialogComponent', () => {
  let component: AttendeesDialogComponent;
  let fixture: ComponentFixture<AttendeesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttendeesDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AttendeesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
