import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CamundaApiMockService {

  allReleases = [
    {
      processInstanceId: "9c2a6203-b41c-11ec-9c4d-00090ffe0001",
      variables: {
          "entwicklungsbranch": {
              "type": "String",
              "value": "03.00",
              "valueInfo": {}
          },
          "prv_release": {
              "type": "String",
              "value": "22.02.00",
              "valueInfo": {}
          },
          "geplanter_lieferzeitpunkt_an_test": {
              "type": "String",
              "value": "20/04/2022",
              "valueInfo": {}
          },
          "vorlagen": {
              "type": "String",
              "value": null,
              "valueInfo": {}
          },
          "notizen": {
              "type": "String",
              "value": "hier sind notizen",
              "valueInfo": {}
          },
          "branch_bezeichnung": {
              "type": "String",
              "value": "RC",
              "valueInfo": {}
          },
          "auslieferungsordner": {
              "type": "String",
              "value": null,
              "valueInfo": {}
          },
          "basisdaten": {
              "type": "String",
              "value": null,
              "valueInfo": {}
          }
      }
    }
  ]

  constructor() { }

  getCurrentReleasesWithVariables() {
    return this.allReleases;
  }
}
