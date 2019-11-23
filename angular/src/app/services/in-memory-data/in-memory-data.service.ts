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
    const dependencies = [
      {id: '1', title: 'Antrag bei Geburt',
      description: 'Dependency chart for the product "Antrag bei Geburt"',
      // tslint:disable-next-line:max-line-length
      chartContent: '"antragBeiGeburt":{"frontend":{"repo": "kgo-antraggeburt-ui"},"backend":{"repo": "kg-online-backend","dependencies":[{"repo": "common-repo"}, {"repo": "soap-clients"}]},"kafka-consumer":{"repo": "kiwi-consumer"}}'},
      {id: '2', title: 'Antrag ab 18',
      description: 'Dependency chart for the product "Antrag ab 18"',
      // tslint:disable-next-line:max-line-length
      chartContent: '"antragAb18": {"frontend": {"repo": "kgo-antragab18-ui"},"backend": {"repo": "kgo-antragab18-backend","dependencies": [{"repo": "common-repo"},{"repo": "soap-clients"},{"repo": "oam-client"}]},"kafka-consumer": {"repo": "kiwi-consumer"}}'},
      {id: '3', title: 'Veraenderungsmitteilung',
      description: 'Dependency chart for the product "Veraenderungsmitteilung"',
      // tslint:disable-next-line:max-line-length
      chartContent: '"veraenderungsmitteilung": {"frontend": {"repo": "kgo-antragab18-ui","dependencies": [{"repo": "aas-client"}]},"backend": {"repo": "kgo-antragab18-backend","dependencies": [{"repo": "soap-clients"}]},"kafka-consumer": {"repo": "kiwi-consumer"}}'}
    ];
    return {batches, dependencies};
  }
}


/*
Copyright Google LLC. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
