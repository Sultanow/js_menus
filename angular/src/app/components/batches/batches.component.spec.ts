import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchesComponent } from './batches.component';
import { BatchService } from 'src/app/services/batches/batches.service';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHandler } from '@angular/common/http';

describe('BatchesComponent', () => {
  let component: BatchesComponent;
  let fixture: ComponentFixture<BatchesComponent>;
  const showBatches: boolean = true;
  const editOn: boolean = false;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BatchesComponent ],
      imports: [
        FormsModule
      ],
      providers: [
        BatchService,
        HttpClient,
        HttpHandler
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    component.editOn = editOn;
    component.showBatches = showBatches;
    expect(component).toBeTruthy();
  });
});
