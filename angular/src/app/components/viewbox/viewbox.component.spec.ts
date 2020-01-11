import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewboxComponent } from './viewbox.component';

describe('ViewboxComponent', () => {
  let component: ViewboxComponent;
  let fixture: ComponentFixture<ViewboxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewboxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
