import { Component, OnInit, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'g[o-north-menu]',
  templateUrl: './north-menu.component.html',
  styleUrls: [ './../../../app.component.css' ]
})
export class NorthMenuComponent implements OnInit, OnChanges {

  @Input() isOpen: boolean;
  @Input() activeItems: string[] = [];

  // Notify parent (app) when details box should open
  @Output() notifyEventOpen = new EventEmitter<string>();

  testActive: boolean = true;
  devActive: boolean = true;
  prodActive: boolean = true;

  constructor () { }
  ngOnChanges(changes: SimpleChanges): void {
    this.reloadActiveItems();
  }

  openTest() {
    if (this.testActive)
      this.notifyEventOpen.emit("test");
  }

  openDev() {
    if (this.devActive)
      this.notifyEventOpen.emit("dev");
  }

  openProd() {
    if (this.prodActive)
      this.notifyEventOpen.emit("prod");
  }

  ngOnInit() {
    this.reloadActiveItems();
  }

  reloadActiveItems() {
    if (this.activeItems.length !== 0) {
      this.testActive = false;
      this.devActive = false;
      this.prodActive = false;
      if (this.activeItems.includes("test")) {
        this.testActive = true;
      }
      if (this.activeItems.includes("dev")) {
        this.devActive = true;
      }
      if (this.activeItems.includes("prod")) {
        this.prodActive = true;
      }
    } else {
      this.testActive = true;
      this.devActive = true;
      this.prodActive = true;
    }
  }
}
