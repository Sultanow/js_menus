import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[o-south-menu]',
  templateUrl: './south-menu.component.html'
})
export class SouthMenuComponent implements OnInit {

  @Input() isOpen: boolean;
  
  constructor() { }

  ngOnInit() {
  }

}
