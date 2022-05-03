import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VersionDataMockService {

  versions = [
    {
      id: "22.02.00",
      rank: "P0 - major"
    },
    {
      id: "22.02.01",
      rank: "P0 - minor"
    },
    {
      id: "22.03.00",
      rank: "P+1 - major"
    },
    {
      id: "22.03.01",
      rank: "P+1 - minor"
    },
  ]

  constructor() { }

  getVersions() {
    return this.versions;
  }
}
