import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'g[o-south-menu]',
  templateUrl: './south-menu.component.html'
})
export class SouthMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  @Output() notifyDependenciesOpen = new EventEmitter<boolean>();
  
  openDetails() {
    this.notifyDependenciesOpen.emit(true);
  }

  constructor() { }

  ngOnInit() {
  }

}
