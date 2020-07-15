import { TestBed } from '@angular/core/testing';

import { CompareItemsService } from './compare-items.service';

describe('CompareItemsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CompareItemsService = TestBed.inject(CompareItemsService);
    expect(service).toBeTruthy();
  });
});
