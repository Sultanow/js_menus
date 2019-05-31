import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SouthMenuComponent } from './south-menu.component';

describe('SouthMenuComponent', () => {
  let component: SouthMenuComponent;
  let fixture: ComponentFixture<SouthMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SouthMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SouthMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
