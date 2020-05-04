import { TestBed } from '@angular/core/testing';
import { BatchService } from './batches.service';
import { HttpHandler, HttpClient } from '@angular/common/http';

describe('BatchService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    declarations: [],
    imports: [
    ],
    providers: [
      HttpClient,
      HttpHandler
    ]
  }).compileComponents()
  );

  it('should be created', () => {
    const service: BatchService = TestBed.get(BatchService);
    expect(service).toBeTruthy();
  });
});
