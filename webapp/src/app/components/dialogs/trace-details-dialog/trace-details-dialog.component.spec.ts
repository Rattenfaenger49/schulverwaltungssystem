import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TraceDetailsDialogComponent } from './trace-details-dialog.component';

describe('TraceDetailsDialogComponent', () => {
  let component: TraceDetailsDialogComponent;
  let fixture: ComponentFixture<TraceDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TraceDetailsDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TraceDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
