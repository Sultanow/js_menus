import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorybetweentowdatesComponent } from './historybetweentowdates.component';

describe('HistorybetweentowdatesComponent', () => {
  let component: HistorybetweentowdatesComponent;
  let fixture: ComponentFixture<HistorybetweentowdatesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HistorybetweentowdatesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HistorybetweentowdatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
