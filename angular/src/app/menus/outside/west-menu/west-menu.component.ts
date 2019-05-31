import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[o-west-menu]',
  templateUrl: './west-menu.component.html'
})
export class WestMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  
  constructor() { }

  ngOnInit() {
  }

}
