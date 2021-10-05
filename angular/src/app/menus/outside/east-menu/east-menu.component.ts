import { Component, OnInit, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'g[o-east-menu]',
  templateUrl: './east-menu.component.html',
  styleUrls: [ './../../../app.component.css' ]
})
export class EastMenuComponent implements OnInit, OnChanges {

  @Input() isOpen: boolean;
  @Output() notifyEventOpen = new EventEmitter<string>();

  @Input() activeItems: string[] = [];

  statisticActive = true;
  newsActive = true;
  warningActive = true;

  constructor () { }
  ngOnChanges(changes: SimpleChanges): void {
    this.reloadActiveItems();
  }

  ngOnInit() {
    this.reloadActiveItems();
  }


  reloadActiveItems() {
    if (this.activeItems.length !== 0) {
      this.statisticActive = false;
      this.newsActive = false;
      this.warningActive = false;
      if (this.activeItems.includes("statistic")) {
        this.statisticActive = true;
      }
      if (this.activeItems.includes("warning")) {
        this.warningActive = true;
      }
      if (this.activeItems.includes("news")) {
        this.newsActive = true;
      }
    } else {
      this.statisticActive = true;
      this.newsActive = true;
      this.warningActive = true;
    }
  }

  openStatistic() {
    if (this.statisticActive)
      this.notifyEventOpen.emit("statistic");
  }

  openNews() {
    if (this.newsActive)
      this.notifyEventOpen.emit("news");
  }

  openWarning() {
    if (this.warningActive)
      this.notifyEventOpen.emit("warning");
  }
}
