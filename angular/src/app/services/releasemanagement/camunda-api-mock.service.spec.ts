import { TestBed } from '@angular/core/testing';

import { CamundaApiMockService } from './camunda-api-mock.service';

describe('CamundaApiMockService', () => {
  let service: CamundaApiMockService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CamundaApiMockService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
