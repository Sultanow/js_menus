import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateChartComponent } from './create-chart.component';

describe('CreateChartComponent', () => {
  let component: CreateChartComponent;
  let fixture: ComponentFixture<CreateChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
