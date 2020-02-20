import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-status-warnings',
  templateUrl: './status-warnings.component.html',
  styleUrls: [ './status-warnings.component.css' ]
})
export class StatusWarningsComponent implements OnInit {

  @Input() showStatusWarnings: boolean;
  scrollingMenu: boolean = false;
  constructor () { }

  ngOnInit() {
  }

}
