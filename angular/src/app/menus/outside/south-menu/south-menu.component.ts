import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'g[o-south-menu]',
  templateUrl: './south-menu.component.html',
  styleUrls: [ './../../../app.component.css' ]
})
export class SouthMenuComponent implements OnInit, OnChanges {

  constructor () { }
  ngOnChanges(changes: SimpleChanges): void {
    this.reloadActiveItems();
  }

  ngOnInit() {
    this.reloadActiveItems();
  }

  @Input() activeItems: string[] = [];
  @Input() isOpen: boolean;
  @Output() notifyEventOpen = new EventEmitter<string>();

  serverconfigActive: boolean = true;
  compareActive: boolean = true;
  batchesActive: boolean = true;
  batchChartActive: boolean = true;

  openConfig() {
    if (this.compareActive)
      this.notifyEventOpen.emit("serverconfig");
  }

  openServer() {
    if (this.serverconfigActive)
      this.notifyEventOpen.emit("compare");
  }

  openBatches() {
    if (this.batchesActive)
      this.notifyEventOpen.emit("batches");
  }

  openBatchChart() {
    if (this.batchChartActive)
      this.notifyEventOpen.emit("batchChart");
  }

  reloadActiveItems() {
    if (this.activeItems.length !== 0) {
      this.serverconfigActive = false;
      this.compareActive = false;
      this.batchesActive = false;
      this.batchChartActive = false;
      if (this.activeItems.includes("configoverview")) {
        this.serverconfigActive = true;
      }
      if (this.activeItems.includes("config")) {
        this.compareActive = true;
      }
      if (this.activeItems.includes("batches")) {
        this.batchesActive = true;
      } 
      if (this.activeItems.includes("batchChart")) {
        this.batchChartActive = true;
      }
    } else {
      this.serverconfigActive = true;
      this.compareActive = true;
      this.batchesActive = true;
      this.batchChartActive = true;
    }
  }

}
