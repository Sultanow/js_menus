import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TickingNewsCardComponent } from './ticking-news-card.component';

describe('TickingNewsCardComponent', () => {
  let component: TickingNewsCardComponent;
  let fixture: ComponentFixture<TickingNewsCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TickingNewsCardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TickingNewsCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
