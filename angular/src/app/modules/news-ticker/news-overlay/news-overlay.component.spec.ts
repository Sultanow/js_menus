import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsOverlayComponent } from './news-overlay.component';

describe('NewsOverlayComponent', () => {
  let component: NewsOverlayComponent;
  let fixture: ComponentFixture<NewsOverlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewsOverlayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewsOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
