import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankInfoDialogComponent } from './bank-info-dialog.component';

describe('BankInfoDialogComponent', () => {
  let component: BankInfoDialogComponent;
  let fixture: ComponentFixture<BankInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BankInfoDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BankInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
