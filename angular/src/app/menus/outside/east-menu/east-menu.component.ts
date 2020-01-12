import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'g[o-east-menu]',
  templateUrl: './east-menu.component.html'
})
export class EastMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  @Output() notifyStatisticOpen = new EventEmitter<boolean>();
  constructor() { }

  ngOnInit() {
  }

  openStatistic() {
    this.notifyStatisticOpen.emit(true);
  }
}
