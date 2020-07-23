import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[m-north-menu]',
  templateUrl: './north-menu.component.html'
})
export class NorthMenuComponent implements OnInit {
  @Input() isOpen = false;
  constructor () { }

  ngOnInit() {
  }

}
