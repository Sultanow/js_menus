import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Configuration } from '../../model/configuration';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: [ './details.component.css' ]
})
export class DetailsComponent implements OnInit {

  configData: Configuration[] = [];

  @Input() showDetails: boolean;
  // Notify parent (app) when details box should close
  @Output() notifyDetailsClose = new EventEmitter<boolean>();

  constructor () { }

  ngOnInit() {
  }

  ngOnChanges(changes) {
    if (this.showDetails) {
      this.getConfigData();
    }
  }

  getConfigData() {
    
  }

  close() {
    this.showDetails = false;
    this.notifyDetailsClose.emit(true);
  }


}
