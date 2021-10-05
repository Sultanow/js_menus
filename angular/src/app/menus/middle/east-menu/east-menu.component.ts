import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[m-east-menu]',
  templateUrl: './east-menu.component.html'
})
export class EastMenuComponent implements OnInit {
  @Input() isOpen = false;

  constructor () { }

  ngOnInit() {
  }

}
