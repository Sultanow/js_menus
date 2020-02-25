import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ViewboxComponent } from './viewbox.component';
import { AppModule } from 'src/app/app.module';

describe('ViewboxComponent', () => {
  let component: ViewboxComponent;
  let fixture: ComponentFixture<ViewboxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [  ],
      imports: [ 
        AppModule 
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
