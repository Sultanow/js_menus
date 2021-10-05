import { TestBed } from '@angular/core/testing';

import { LoadChartDataService } from './load-chart-data.service';

describe('LoadChartDataService', () => {
  let service: LoadChartDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadChartDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
