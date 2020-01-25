import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'g[o-west-menu]',
  templateUrl: './west-menu.component.html'
})
export class WestMenuComponent implements OnInit {

  @Input() isOpen: boolean;

  @Output() notifyEventOpen = new EventEmitter<string>();


  constructor() { }

  ngOnInit() {
  }

  openConfluence() {
    this.notifyEventOpen.emit("confluence");
  }

  openJira() {
    this.notifyEventOpen.emit("jira");
  }

  openBitbucket() {
    this.notifyEventOpen.emit("bitbucket");
  }

}
