import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsDisplayComponent } from './news-display.component';

describe('NewsDisplayComponent', () => {
  let component: NewsDisplayComponent;
  let fixture: ComponentFixture<NewsDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewsDisplayComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewsDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
