import { Component, Input, OnInit } from '@angular/core';
import { LoadChartDataService } from 'src/app/services/load-chart-data/load-chart-data.service';
import {GraphProcessor} from "src/app/components/batch-charts/graph-processor/graph-processor";

@Component({
  selector: 'app-batch-charts',
  templateUrl: './batch-charts.component.html',
  styleUrls: ['./batch-charts.component.css']
})
export class BatchChartsComponent implements OnInit {
  @Input() showBatchChart: boolean;

  constructor(private loadChartDataService: LoadChartDataService) { }

  public searchTerm: string;

  private processedGraphData: any= {
    inited: false,
    nodes: [],
    links: [],
    directDependantNodes:[]
  };


  async ngOnInit() {
    // getting graph data from load-chart-data.service
    let graphData = await this.loadChartDataService.getGraphData();
    console.log("Next message wit hgraph data is here!")
    let graphProcessor = new GraphProcessor(graphData);
    //processing returned data -> putting cycle and difference graph markers into nodes and edges
    this.processedGraphData = graphProcessor.processAndReturnGraphData();
    this.loadChartDataService.nextMessage(this.processedGraphData);
  }

  receiveSearchTerm($event): void {
    this.searchTerm = $event;
  }

}
