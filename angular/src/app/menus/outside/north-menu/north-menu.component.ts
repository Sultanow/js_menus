import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[o-north-menu]',
  templateUrl: './north-menu.component.html'
})
export class NorthMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  
  constructor() { }

  ngOnInit() {
    console.log(this.isOpen);
  }

}
