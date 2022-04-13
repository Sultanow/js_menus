import { TestBed } from '@angular/core/testing';

import { CamundaApiConnectorService } from './camunda-api-connector.service';

describe('CamundaApiConnectorService', () => {
  let service: CamundaApiConnectorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CamundaApiConnectorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
