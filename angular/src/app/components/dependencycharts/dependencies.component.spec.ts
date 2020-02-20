import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DependencyChartsComponent } from './dependencycharts.component';

describe('DependencyChartsComponent', () => {
  let component: DependencyChartsComponent;
  let fixture: ComponentFixture<DependencyChartsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DependencyChartsComponent ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DependencyChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
