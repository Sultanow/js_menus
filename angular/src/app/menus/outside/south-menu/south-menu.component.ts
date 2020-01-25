import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'g[o-south-menu]',
  templateUrl: './south-menu.component.html'
})
export class SouthMenuComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  @Input() isOpen: boolean;
  @Output() notifyEventOpen = new EventEmitter<string>();
  
  openConfig() {
    this.notifyEventOpen.emit("dependency");
  }

  openServer() {
    this.notifyEventOpen.emit("server");
  }

  openBatches() {
    this.notifyEventOpen.emit("batches");
  }

  
}
