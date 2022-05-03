import { TestBed } from '@angular/core/testing';

import { VersionDataMockService } from './version-data-mock.service';

describe('VersionDataMockService', () => {
  let service: VersionDataMockService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VersionDataMockService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
