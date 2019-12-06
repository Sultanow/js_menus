import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'g[o-south-menu]',
  templateUrl: './south-menu.component.html'
})
export class SouthMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  @Output() notifyDependencyChartsOpen = new EventEmitter<boolean>();
  
  openDetails() {
    this.notifyDependencyChartsOpen.emit(true);
  }

  constructor() { }

  ngOnInit() {
  }

}
