import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DependencyChartsComponent } from './dependencycharts.component';
import { MatDialog } from '@angular/material/dialog';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { Overlay } from '@angular/cdk/overlay';

describe('DependencyChartsComponent', () => {
  let component: DependencyChartsComponent;
  let fixture: ComponentFixture<DependencyChartsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DependencyChartsComponent ],
      imports: [
        MatCardModule,
      ],
      providers: [
        HttpClient,
        HttpHandler,
        { provide: MatDialog, useValue: {} },
        Overlay,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DependencyChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
