import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[m-south-menu]',
  templateUrl: './south-menu.component.html'
})
export class SouthMenuComponent implements OnInit {
  @Input() isOpen = false;
  
  constructor () { }

  ngOnInit() {
  }

}
