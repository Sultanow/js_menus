import { Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { StatisticGroup } from './model/statisticGroup';
import { StatisticService } from './services/statistic.service';
import { StatisticChart } from './model/statisticChart';

@Component({
  selector: 'app-statistic',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './statistic.component.html',
  styleUrls: [ './statistic.component.css' ]
})
export class StatisticComponent implements OnInit, OnChanges {
  // In-/Outputs
  @Input() showStatistic: boolean;
  @Output() notifyTitle = new EventEmitter<string>();
  // Variables
  allCharts: StatisticGroup[] = [];
  showCreateChartContainer = false;
  showGraphChart = false;
  chartName = "";
  activeChart: StatisticChart = null;

  constructor (private statisticService: StatisticService) { }

  ngOnInit(): void {
    if (this.showStatistic) {
      this.reloadData();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.showStatistic) {
      this.reloadData();
      this.notifyTitle.emit("Statistiken");
    }
  }

  reloadData() {
    this.statisticService.getChartNames().subscribe(result => {
      this.allCharts = result;
      if (this.showGraphChart === false && this.showCreateChartContainer === false) {
        if (this.allCharts != null && this.allCharts.length > 0) {
          this.showChartView(null, Object.keys(this.allCharts[ 0 ].charts)[ 0 ]);
        }
        else {
          this.createChartView(null);
        }
      }
    });
  }

  createChartView(event) {
    this.showCreateChartContainer = true;
    this.showGraphChart = false;
    this.chartName = "";
    this.activeChart = null;
  }

  showChartView(event, chart) {
    this.showGraphChart = true;
    this.chartName = chart;
    this.showCreateChartContainer = false;
    this.activeChart = this.getActiveChart(chart);
  }

  private getActiveChart(chart: string): StatisticChart {
    let chartItem;
    this.allCharts.forEach(x => {
      if (x.charts[ chart ])
        return chartItem = x.charts[ chart ];
    });
    return chartItem;
  }

  onNewChartSubmitted() {
    this.reloadData();
  }

  onDeleteChart() {
    this.chartName = "";
    this.showGraphChart = false;
    this.reloadData();
  }
}
