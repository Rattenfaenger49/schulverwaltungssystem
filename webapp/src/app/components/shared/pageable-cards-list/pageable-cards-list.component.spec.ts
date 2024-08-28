import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageableCardsListComponent } from './pageable-cards-list.component';

describe('PageableCardsListComponent', () => {
  let component: PageableCardsListComponent;
  let fixture: ComponentFixture<PageableCardsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageableCardsListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PageableCardsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
