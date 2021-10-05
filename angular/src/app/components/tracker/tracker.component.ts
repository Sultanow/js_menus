import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { TrackerService } from 'src/app/services/tracker/tracker.service';
import { TrackerInfos } from 'src/app/model/trackerInfos';

@Component({
  selector: 'app-tracker',
  templateUrl: './tracker.component.html',
  styleUrls: [ './tracker.component.css', '../viewbox/viewbox.component.css' ]
})
export class TrackerComponent implements OnInit {

  @Input() showTracker: boolean;
  @Input() viewType: string;
  @Output() notifyTitle = new EventEmitter<string>();

  infos: TrackerInfos[];
  headline: string;

  constructor (private trackerService: TrackerService) { }

  ngOnInit() {
  }
  ngOnChanges(changes) {
    if (this.showTracker) {
      if (this.viewType === "jira") {
        this.headline = "Jira Arbeitspakete mit Status";
        this.infos = this.trackerService.getJiraInfos();
        this.notifyTitle.emit("Jira");
      } else if (this.viewType === "bitbucket") {
        this.headline = "Bitbucket Pullrequests";
        this.infos = this.trackerService.getBitbucketInfos();
        this.notifyTitle.emit("Bitbucket");
      } else if (this.viewType === "confluence") {
        this.headline = "Confluence Seiten mit Status";
        this.infos = this.trackerService.getConfluenceInfos();
        this.notifyTitle.emit("Confluence");
      }
    }
  }
}
