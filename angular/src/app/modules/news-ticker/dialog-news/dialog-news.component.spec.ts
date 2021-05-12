import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogNewsComponent } from './dialog-news.component';

describe('DialogNewsComponent', () => {
  let component: DialogNewsComponent;
  let fixture: ComponentFixture<DialogNewsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogNewsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogNewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
