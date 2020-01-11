import { Component, OnInit, Output, Input, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';

@Component({
  selector: 'app-viewbox',
  templateUrl: './viewbox.component.html',
  styleUrls: ['./viewbox.component.css']
})
export class ViewboxComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  ngOnChange(changes) {
    console.log(changes);
    console.log("showView String is: " + this.showView);
    this.openShowView();
  }

  @Input() showViewBox: boolean;
  @Input() showView: string;
  // Notify parent (app) when details box should close
  @Output() notifyViewBoxClose = new EventEmitter<boolean>();

  showCompare: boolean = false;

  close(){
    this.showViewBox = false;
    this.notifyViewBoxClose.emit(true);
    this.closeAllViews();

  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes)
    if(changes.showView && changes.showView.currentValue === "compare")
    {
      this.openShowView();
    }
   }

  openShowView() {
    this.showCompare = true;
  }

  closeAllViews() {
    this.showCompare = false;
  }
}
