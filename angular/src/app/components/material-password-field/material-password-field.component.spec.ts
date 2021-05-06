import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialPasswordFieldComponent } from './material-password-field.component';

describe('MaterialPasswordFieldComponent', () => {
  let component: MaterialPasswordFieldComponent;
  let fixture: ComponentFixture<MaterialPasswordFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MaterialPasswordFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MaterialPasswordFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
