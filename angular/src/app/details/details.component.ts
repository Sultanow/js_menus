import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  @Input() showDetails: boolean;

  // Notify parent (app) when details box should close
  @Output() notifyDetailsClose = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit() {
  }

  close(){
    this.showDetails = false;
    this.notifyDetailsClose.emit(true);
  }


}
