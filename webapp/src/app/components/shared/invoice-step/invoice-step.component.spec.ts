import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceStepComponent } from './invoice-step.component';

describe('InvoiceStepComponent', () => {
  let component: InvoiceStepComponent;
  let fixture: ComponentFixture<InvoiceStepComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceStepComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InvoiceStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
