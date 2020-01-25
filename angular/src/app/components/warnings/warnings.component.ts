import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-warnings',
  templateUrl: './warnings.component.html',
  styleUrls: ['./warnings.component.css']
})
export class WarningsComponent implements OnInit {

  @Input() showWarnings: boolean;
  
  constructor() { }

  ngOnInit() {
  }

}
