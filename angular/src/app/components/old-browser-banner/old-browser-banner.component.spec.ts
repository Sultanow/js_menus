import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OldBrowserBannerComponent } from './old-browser-banner.component';

describe('OldBrowserBannerComponent', () => {
  let component: OldBrowserBannerComponent;
  let fixture: ComponentFixture<OldBrowserBannerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OldBrowserBannerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OldBrowserBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
