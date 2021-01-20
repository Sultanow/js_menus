import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartNodepsComponent } from './chart-nodeps.component';

describe('ChartNodepsComponent', () => {
  let component: ChartNodepsComponent;
  let fixture: ComponentFixture<ChartNodepsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChartNodepsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartNodepsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
