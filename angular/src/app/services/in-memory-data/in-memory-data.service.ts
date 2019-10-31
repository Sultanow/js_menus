import { InMemoryDbService } from 'angular-in-memory-web-api';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class InMemoryDataService implements InMemoryDbService {
  createDb() {
    const batches = [
      { id: 'a220', duration: 6 },
      { id: 'a610', duration: 28 },
      { id: 'a620', duration: 496 },
      { id: 'a999', duration: 8128 },
    ];
    return {batches};
  }
}


/*
Copyright Google LLC. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
