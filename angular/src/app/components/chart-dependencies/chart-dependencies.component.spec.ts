import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartDependenciesComponent } from './chart-dependencies.component';

describe('ChartDependenciesComponent', () => {
  let component: ChartDependenciesComponent;
  let fixture: ComponentFixture<ChartDependenciesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChartDependenciesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartDependenciesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
