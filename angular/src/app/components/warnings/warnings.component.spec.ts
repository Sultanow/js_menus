import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WarningsComponent } from './warnings.component';
import { AppModule } from 'src/app/app.module';

describe('WarningsComponent', () => {
  let component: WarningsComponent;
  let fixture: ComponentFixture<WarningsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ],
      imports: [
        AppModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WarningsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
