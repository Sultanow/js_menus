import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EastMenuComponent } from './east-menu.component';

describe('EastMenuComponent', () => {
  let component: EastMenuComponent;
  let fixture: ComponentFixture<EastMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EastMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EastMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
