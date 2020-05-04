import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'g[o-west-menu]',
  templateUrl: './west-menu.component.html',
  styleUrls: [ './../../../app.component.css' ]
})
export class WestMenuComponent implements OnInit, OnChanges {

  @Input() activeItems: string[] = [];
  @Input() isOpen: boolean;
  @Output() notifyEventOpen = new EventEmitter<string>();

  confluenceActive: boolean = true;
  jiraActive: boolean = true;
  bitbucketActive: boolean = true;

  constructor () { }
  ngOnChanges(changes: SimpleChanges): void {
    this.reloadActiveItems();
  }

  ngOnInit() {
    this.reloadActiveItems();
  }

  openConfluence() {
    if (this.confluenceActive)
      this.notifyEventOpen.emit("confluence");
  }

  openJira() {
    if (this.jiraActive)
      this.notifyEventOpen.emit("jira");
  }

  openBitbucket() {
    if (this.bitbucketActive)
      this.notifyEventOpen.emit("bitbucket");
  }

  reloadActiveItems() {
    if (this.activeItems.length !== 0) {
      this.confluenceActive = false;
      this.jiraActive = false;
      this.bitbucketActive = false;
      if (this.activeItems.includes("confluence")) {
        this.confluenceActive = true;
      }
      if (this.activeItems.includes("jira")) {
        this.jiraActive = true;
      }
      if (this.activeItems.includes("bitbucket")) {
        this.bitbucketActive = true;
      }
    } else {
      this.confluenceActive = true;
      this.jiraActive = true;
      this.bitbucketActive = true;
    }
  }


}
