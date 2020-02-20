import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NorthMenuComponent } from './north-menu.component';

describe('NorthMenuComponent', () => {
  let component: NorthMenuComponent;
  let fixture: ComponentFixture<NorthMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NorthMenuComponent ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NorthMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
