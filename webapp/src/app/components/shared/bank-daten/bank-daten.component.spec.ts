import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankDatenComponent } from './bank-daten.component';

describe('BankDatenComponent', () => {
  let component: BankDatenComponent;
  let fixture: ComponentFixture<BankDatenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BankDatenComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BankDatenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
