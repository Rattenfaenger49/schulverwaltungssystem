import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonComponent } from './lesson.component';

describe('LessonComponent', () => {
  let component: LessonComponent;
  let fixture: ComponentFixture<LessonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [LessonComponent]
});
    fixture = TestBed.createComponent(LessonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
