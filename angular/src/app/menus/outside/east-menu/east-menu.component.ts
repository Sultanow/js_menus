import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'g[o-east-menu]',
  templateUrl: './east-menu.component.html'
})
export class EastMenuComponent implements OnInit {

  @Input() isOpen: boolean;

  @Output() notifyCompareOpen = new EventEmitter<boolean>();
  
  constructor() { }

  openCompare() {
    console.log("Click compare");
    this.notifyCompareOpen.emit(true);
}
  ngOnInit() {
  }

}
