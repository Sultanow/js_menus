import { Injectable } from '@angular/core';
import { TrackerInfos } from 'src/app/model/trackerInfos';

@Injectable({
  providedIn: 'root'
})
export class TrackerService {

  constructor () { }

  getJiraInfos(): TrackerInfos[] {
    return this.dummyJiraInfos();
  }

  getBitbucketInfos(): TrackerInfos[] {
    return this.dummyBitbucketInfos();
  }

  getConfluenceInfos(): TrackerInfos[] {
    return this.dummyConflucenceInfos();
  }

  generateInfos(k: string, v: string): TrackerInfos {
    return new TrackerInfos(k, v);
  }

  dummyJiraInfos(): TrackerInfos[] {
    let infos: TrackerInfos[] = [];
    infos.push(this.generateInfos("Open", "20"));
    infos.push(this.generateInfos("In Analyse", "20"));
    infos.push(this.generateInfos("Geschlossen", "20"));
    return infos;
  }

  dummyBitbucketInfos(): TrackerInfos[] {
    let infos: TrackerInfos[] = [];
    infos.push(this.generateInfos("Offen", "10"));
    infos.push(this.generateInfos("Konflikte", "10"));
    infos.push(this.generateInfos("Abschlossen", "10"));
    return infos;
  }

  dummyConflucenceInfos(): TrackerInfos[] {
    let infos: TrackerInfos[] = [];
    infos.push(this.generateInfos("Benötigt Überarbeitung", "120"));
    infos.push(this.generateInfos("Aktuell", "3034"));

    return infos;
  }
}
