import { TestBed } from '@angular/core/testing';

import { ConfigurationItemsService } from './configuration-items.service';

describe('ConfigurationItemsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ConfigurationItemsService = TestBed.get(ConfigurationItemsService);
    expect(service).toBeTruthy();
  });
});
