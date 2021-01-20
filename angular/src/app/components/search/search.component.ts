import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { LoadChartDataService } from '../../services/load-chart-data/load-chart-data.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  public static readonly SEARCH_RESET = "[RESET]";
  public static readonly SEARCH_FILTER_NODEPS = "[FILTER_NODEPS]";

  @Input() searchTerm : string;
  @Input() dependantBatchesOnly : boolean;
  @Output() searchEvent = new EventEmitter<string>();

  constructor(private loadChartDataService: LoadChartDataService) { }

  ngOnInit() {
    this.loadChartDataService.chartMessage.subscribe(graphData => {
      if (graphData != null) {
        console.log("SearchComponent: graphData received " + graphData);
      }});
  }

  search() {
    if (this.searchTerm != null && this.searchTerm.length > 2) {
      this.searchEvent.emit(this.searchTerm)
    }
  }

  reset() {
    this.searchEvent.emit(SearchComponent.SEARCH_RESET);
  }

  toggleDependantBatches() {
    if (this.dependantBatchesOnly) {
      this.searchEvent.emit(SearchComponent.SEARCH_FILTER_NODEPS);
    } else {
      this.searchEvent.emit(SearchComponent.SEARCH_RESET);
    }
  }
}
