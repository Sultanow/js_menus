import { Component, Input, OnInit } from '@angular/core';
import { LoadChartDataService } from 'src/app/services/load-chart-data/load-chart-data.service';

@Component({
  selector: 'app-batch-charts',
  templateUrl: './batch-charts.component.html',
  styleUrls: ['./batch-charts.component.css']
})
export class BatchChartsComponent implements OnInit {
  @Input() showBatchChart: boolean;

  constructor(private loadChartDataService: LoadChartDataService) { }

  public searchTerm: string;

  async ngOnInit() {
    let graphData = await this.loadChartDataService.getGraphData();
    this.loadChartDataService.nextMessage(graphData);
  }

  receiveSearchTerm($event): void {
    this.searchTerm = $event;
  }

}
