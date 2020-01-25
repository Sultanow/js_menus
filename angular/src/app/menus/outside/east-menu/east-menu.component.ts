import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'g[o-east-menu]',
  templateUrl: './east-menu.component.html'
})
export class EastMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  @Output() notifyStatisticOpen = new EventEmitter<boolean>();
  @Output() notifyNewsOpen = new EventEmitter<boolean>();
  @Output() notifyWarningOpen = new EventEmitter<boolean>();
  @Output() notifyEventOpen = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  openStatistic() {
    //this.notifyStatisticOpen.emit(true);
    this.notifyEventOpen.emit("statistic");
  }

  openNews() {
    this.notifyEventOpen.emit("news");
    //this.notifyNewsOpen.emit(true);
  }

  openWarning() {
    this.notifyEventOpen.emit("warning");
    //this.notifyWarningOpen.emit(true);
  }
}
