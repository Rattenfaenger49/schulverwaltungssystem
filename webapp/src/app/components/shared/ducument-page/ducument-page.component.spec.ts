import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DucumentPageComponent } from './ducument-page.component';

describe('DucumentPageComponent', () => {
  let component: DucumentPageComponent;
  let fixture: ComponentFixture<DucumentPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DucumentPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DucumentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
