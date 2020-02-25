import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NorthMenuComponent } from './north-menu.component';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { HttpHandler, HttpClient } from '@angular/common/http';

describe('NorthMenuComponent', () => {
  let component: NorthMenuComponent;
  let fixture: ComponentFixture<NorthMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NorthMenuComponent ],
      providers: [
        ConfigurationService,
        HttpHandler,
        HttpClient
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NorthMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
