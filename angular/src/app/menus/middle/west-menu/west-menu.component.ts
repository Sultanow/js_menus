import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[m-west-menu]',
  templateUrl: './west-menu.component.html'
})
export class WestMenuComponent implements OnInit {
  @Input() isOpen = false;
  
  constructor () { }

  ngOnInit() {
  }

}
