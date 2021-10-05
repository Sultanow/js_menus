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
  public static readonly SEARCH_DIFFERENCE_GRAPH = "[DIFFERENCE_GRAPH]";
  public static readonly SEARCH_GRAPH_CYCLES = "[GRAPH_CYCLES]";
  public static readonly SEARCH_GRAPH_CYCLES_ONLY = "[GRAPH_CYCLES_ONLY]";

  @Input() searchTerm : string;
  @Input() dependantBatchesOnly : boolean;
  @Input() batchCycles: boolean;
  @Input() batchCyclesOnly: boolean;
  @Input() differenceGraph: boolean;
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
      console.log("Toggled dependant batches!")
      this.searchEvent.emit(SearchComponent.SEARCH_FILTER_NODEPS);
    } else {
      this.searchEvent.emit(SearchComponent.SEARCH_RESET);
    }
  }
  showDifferenceGraph() {
    if (this.differenceGraph) {
      console.log("Toggled  difference graph!")
      this.searchEvent.emit(SearchComponent.SEARCH_DIFFERENCE_GRAPH);
    } else {
      this.searchEvent.emit(SearchComponent.SEARCH_RESET);
    }
  }
  showGraphCycleDependency() {
    if (this.batchCycles) {
      console.log("Toggled graph cycles!!")
      this.searchEvent.emit(SearchComponent.SEARCH_GRAPH_CYCLES);
    } else {
      this.searchEvent.emit(SearchComponent.SEARCH_RESET);
    }
  }

  showGraphCyclesOnly(){
    if (this.batchCyclesOnly) {
      console.log("Toggled isolated graph cycles!!")
      this.searchEvent.emit(SearchComponent.SEARCH_GRAPH_CYCLES_ONLY);
    } else {
      this.searchEvent.emit(SearchComponent.SEARCH_RESET);
    }
  }
}
