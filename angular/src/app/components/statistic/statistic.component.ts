import { Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { Batches } from 'src/app/model/batches';
import { StatisticService } from 'src/app/services/statistic/statistic.service';
import { StatisticGroup } from 'src/app/model/statisticGroups';



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
  charts: StatisticGroup[] = [];
  showCreateChartContainer = false;
  showGraphChart = false;
  chartName = "";

  activeElement: Element = null;
  // Inits

  ngOnInit(): void {
    this.reloadData();
  }

  constructor (private statisticService: StatisticService) { }

  // Methods
  ngOnChanges(changes: SimpleChanges): void {
    this.reloadData();
    if (this.showStatistic) {
      this.notifyTitle.emit("Statistiken");
    }
  }

  reloadData() {
    this.statisticService.getChartNames().subscribe(result => {
      console.log(result);
      this.charts = result;
      if (this.showGraphChart === false && this.showCreateChartContainer === false) {
        if (this.charts.length > 0 && this.charts[ 0 ].charts.length > 0)
          this.showChartView(null, this.charts[ 0 ].charts[ 0 ]);
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
  }

  showChartView(event, chart) {
    this.showGraphChart = true;
    this.chartName = chart;
    this.showCreateChartContainer = false;
  }

  onNewChartSubmitted() {
    this.reloadData();
  }

  onDeleteChart() {
    this.chartName = "";
    this.showGraphChart = false;
    this.activeElement = null;
    this.reloadData();
  }
  // Old

  @Input()
  batchtimes: Batches[];
}
