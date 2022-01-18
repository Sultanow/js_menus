import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteNewsComponent } from './dialog-delete-news.component';

describe('DialogDeleteNewsComponent', () => {
  let component: DialogDeleteNewsComponent;
  let fixture: ComponentFixture<DialogDeleteNewsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteNewsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteNewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
