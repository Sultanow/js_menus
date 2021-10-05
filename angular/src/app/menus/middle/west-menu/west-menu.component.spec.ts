import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WestMenuComponent } from './west-menu.component';

describe('WestMenuComponent', () => {
  let component: WestMenuComponent;
  let fixture: ComponentFixture<WestMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WestMenuComponent ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WestMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
