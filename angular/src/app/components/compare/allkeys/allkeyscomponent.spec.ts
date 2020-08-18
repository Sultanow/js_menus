import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllkeysComponent } from './allkeys.component';

describe('AllKeysComponent', () => {
  let component: AllkeysComponent;
  let fixture: ComponentFixture<AllkeysComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AllkeysComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllkeysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
