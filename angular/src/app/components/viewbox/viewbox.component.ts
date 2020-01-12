import { Component, OnInit, Output, Input, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';
import { Batches } from 'src/app/model/batches';

@Component({
  selector: 'app-viewbox',
  templateUrl: './viewbox.component.html',
  styleUrls: ['./viewbox.component.css']
})
export class ViewboxComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  @Input() showViewBox: boolean;
  @Input() showView: string;
  // Notify parent (app) when details box should close
  @Output() notifyViewBoxClose = new EventEmitter<boolean>();

  showCompare: boolean = false;
  showStatistic: boolean = false;
  batchtimes: Batches[] = [
    {"date": "10-05-2012", "durationBatch1": 68.55, "durationBatch2": 74.55},
    {"date": "09-05-2012", "durationBatch1": 74.55, "durationBatch2": 69.55},
    {"date": "08-05-2012", "durationBatch1": 69.55, "durationBatch2": 62.55},
    {"date": "07-05-2012", "durationBatch1": 62.55, "durationBatch2": 56.55},
    {"date": "06-05-2012", "durationBatch1": 56.55, "durationBatch2": 59.55},
    {"date": "05-05-2012", "durationBatch1": 59.86, "durationBatch2": 65.86},
    {"date": "04-05-2012", "durationBatch1": 62.62, "durationBatch2": 65.62},
    {"date": "03-05-2012", "durationBatch1": 64.48, "durationBatch2": 60.48},
    {"date": "02-05-2012", "durationBatch1": 60.98, "durationBatch2": 55.98},
    {"date": "01-05-2012", "durationBatch1": 58.13, "durationBatch2": 53.13},
    {"date": "30-04-2012", "durationBatch1": 68.55, "durationBatch2": 74.55},
    {"date": "29-04-2012", "durationBatch1": 74.55, "durationBatch2": 69.55},
    {"date": "28-04-2012", "durationBatch1": 69.55, "durationBatch2": 62.55},
    {"date": "27-04-2012", "durationBatch1": 62.55, "durationBatch2": 56.55},
    {"date": "26-04-2012", "durationBatch1": 56.55, "durationBatch2": 59.55},
    {"date": "25-04-2012", "durationBatch1": 59.86, "durationBatch2": 65.86},
    {"date": "24-04-2012", "durationBatch1": 62.62, "durationBatch2": 65.62},
    {"date": "23-04-2012", "durationBatch1": 64.48, "durationBatch2": 60.48},
    {"date": "22-04-2012", "durationBatch1": 60.98, "durationBatch2": 55.98},
    {"date": "21-04-2012", "durationBatch1": 58.13, "durationBatch2": 53.13}
  ];

  loadBatches: Batches[] = [];

  close(){
    this.showViewBox = false;
    this.notifyViewBoxClose.emit(true);
    this.closeAllViews();

  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes)
    if(changes.showView)
    {
      this.openShowView(changes.showView.currentValue);
    } 
   }

  openShowView(view: string) {
    if(view === "compare") {
    this.showCompare = true;
    } else if (view === "statistic") {
      this.showStatistic = true;
      this.loadBatches = this.batchtimes;
    }
  }

  closeAllViews() {
    this.showCompare = false;
    this.showStatistic = false;
  }
}
