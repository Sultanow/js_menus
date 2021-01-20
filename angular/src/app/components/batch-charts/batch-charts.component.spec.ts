import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchChartsComponent } from './batch-charts.component';

describe('BatchChartsComponent', () => {
  let component: BatchChartsComponent;
  let fixture: ComponentFixture<BatchChartsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BatchChartsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
