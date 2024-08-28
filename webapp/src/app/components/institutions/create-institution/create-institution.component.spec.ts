import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateInstitutionComponent } from './create-institution.component';

describe('CreateInstitutionComponent', () => {
  let component: CreateInstitutionComponent;
  let fixture: ComponentFixture<CreateInstitutionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [CreateInstitutionComponent]
});
    fixture = TestBed.createComponent(CreateInstitutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
