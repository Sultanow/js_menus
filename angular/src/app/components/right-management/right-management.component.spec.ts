import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RightManagementComponent } from './right-management.component';

describe('RightManagementComponent', () => {
  let component: RightManagementComponent;
  let fixture: ComponentFixture<RightManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RightManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RightManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
