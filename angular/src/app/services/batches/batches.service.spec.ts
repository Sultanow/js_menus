import { TestBed } from '@angular/core/testing';

import { BatchService } from './batches.service';

describe('BatchService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BatchService = TestBed.get(BatchService);
    expect(service).toBeTruthy();
  });
});
