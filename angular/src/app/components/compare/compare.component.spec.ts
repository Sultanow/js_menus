import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompareComponent } from './compare.component';
import { TreetableModule } from '../treetable/treetable.module';

describe('CompareComponent', () => {
  let component: CompareComponent;
  let fixture: ComponentFixture<CompareComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompareComponent ],
      imports: [ TreetableModule ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
