import { Component, OnInit, ViewChild, ViewEncapsulation, ElementRef, Input } from '@angular/core';
import { Batches } from 'src/app/model/batches';
import { StatisticService } from 'src/app/services/statistic/statistic.service';

@Component({
  selector: 'app-statistic',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './statistic.component.html',
  styleUrls: [ './statistic.component.css' ]
})
export class StatisticComponent implements OnInit {
  // Inputs
  @Input()
  showStatistic: boolean;
  // Variables
  charts = [];
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
  ngOnChange(): void {
    this.reloadData();
  }

  reloadData() {
    this.statisticService.getChartNames().subscribe(result => {
      console.log(result);
      if (result.length > 0)
        this.charts = result.substring(1, result.length - 1).split(", ");

    });
  }

  createChartView(event) {
    console.log(event);
    console.log("createChartView()");
    this.showCreateChartContainer = true;
    this.showGraphChart = false;
    this.chartName = "";
    this.markActiveButton(event.target);
  }

  showChartView(event, chart) {
    console.log(event);
    console.log(chart);
    this.showGraphChart = true;
    this.chartName = chart;
    this.showCreateChartContainer = false;
    this.markActiveButton(event.target);


  }

  markActiveButton(newButton: Element) {
    if (this.activeElement)
      this.activeElement.classList.remove("chartSideNavActive");
    this.activeElement = newButton;
    this.activeElement.classList.add("chartSideNavActive");
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
