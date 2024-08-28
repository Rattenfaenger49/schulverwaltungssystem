import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModulDialogComponent } from './modul-dialog.component';

describe('ModulDialogComponent', () => {
  let component: ModulDialogComponent;
  let fixture: ComponentFixture<ModulDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [ModulDialogComponent]
});
    fixture = TestBed.createComponent(ModulDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
