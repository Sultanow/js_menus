import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { SpinnerService } from 'src/app/services/spinner/spinner.service';
import { StatisticService } from '../services/statistic.service';
import { StatisticChart } from '../model/statisticChart';
import { StatisticAccuracy } from '../model/statisticAccuracy';
import { StatisticData, DEFAULT_LAYOUT } from '../model/statisticData';

declare var Plotly: any;

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: [ './graphs.component.css' ]
})
export class GraphsComponent implements OnInit, OnChanges {
  @Input() showGraphChart: boolean;
  @Input() chart: StatisticChart;
  @Input() chartName: string;

  @Output() notifyDeleteChart = new EventEmitter<boolean>();

  @ViewChild("Graph")
  private plotlyGraph: ElementRef;

  graphData: string;

  actuallStatisticData: StatisticData = null;
  nextStatisticData: StatisticData = null;

  updateTime: string = "";
  fileUploadText: string = "Daten aktualisieren";
  showUploadAreaFile: boolean = false;

  // DatePicker Properties
  datePickerTitle = "Datum";
  timeSeriesDates: string[] = [];


  constructor (
    private statisticService: StatisticService,
    public dialog: MatDialog,
    private spinnerService: SpinnerService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.destroyPlot();
    this.updateTime = "";
    this.loadInitialGraphData();
  }

  destroyPlot() {
    if (this.plotlyGraph && this.plotlyGraph.nativeElement)
      Plotly.purge(this.plotlyGraph.nativeElement);
  }

  loadInitialGraphData() {
    if (this.chartName !== "") {
      this.spinnerService.show();
      this.statisticService.getChartData(this.chartName).subscribe(result => {
        if (result !== null) {
          this.actuallStatisticData = this.nextStatisticData = result;
          this.parseChartData();
        }
        this.spinnerService.hide();
      }, e => {
        this.spinnerService.hide();
        console.log("Could not get Data from backend!");
      });

      if (this.isTimeseries()) {
        this.statisticService.getTimeseriesDates(this.chartName).subscribe(result => {
          console.log(result);
          this.timeSeriesDates = result;
        }, e => {
          console.log("Could not get Timestamps for timeseries!");
        });
      }
    }
  }

  parseChartData() {
    if (this.actuallStatisticData.layout === null || this.nextStatisticData.layout === undefined) {
      this.actuallStatisticData.layout = DEFAULT_LAYOUT;
    }
    this.actuallStatisticData.layout[ 'title' ] = this.actuallStatisticData.title;
    // Is something els to do?
    this.basicChart();
  }

  basicChart(): void {
    this.destroyPlot();
    const element = this.plotlyGraph.nativeElement;
    const graphData = this.actuallStatisticData.traces;
    const layout = this.actuallStatisticData.layout;
    Plotly.newPlot(element, graphData, layout);
  }

  ngOnInit() {
    console.log("Created chart");
    this.loadInitialGraphData();
  }

  onFileAdded(file) {
    this.spinnerService.show();
    this.statisticService.updateData(this.chartName, file).subscribe(result => {
      console.log(result);
      this.loadInitialGraphData();
      this.spinnerService.hide();
    }, e => {
      this.spinnerService.hide();
    });
  }

  deleteChart() {
    this.statisticService.deleteChart(this.chartName).subscribe(result => {
      this.notifyDeleteChart.emit(true);
    });
  }

  openDeleteDialog(): void {
    const dialogRef = this.dialog.open(DialogDeleteChart, {
      width: '250px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteChart();
      }
    });
  }

  // DatePicker Methods
  /**
   * Check if the chart is a timeseries chart.
   */
  isTimeseries(): boolean {
    return (this.chart !== null && this.chart !== undefined) && this.chart.timeseries === true;
  }

  /**
   * Check if multiple is set, for this check if isTimeseries() which is checking if chart is not null
   */
  isMultiple(): boolean {
    return this.isTimeseries && this.chart.multiple == true;
  }

  getAccuracyForString(strAccuracy: string): StatisticAccuracy {
    let accuracy: StatisticAccuracy;
    switch (strAccuracy) {
      case "day":
        accuracy = StatisticAccuracy.DAY;
        break;
      case "week":
        accuracy = StatisticAccuracy.WEEK;
        break;
      case "month":
        accuracy = StatisticAccuracy.MONTH;
        break;
      case "year":
        accuracy = StatisticAccuracy.YEAR;
        break;
      default:
        accuracy = StatisticAccuracy.NONE;
    }
    return accuracy;
  }

  prevTimeseriesChart(): void {
    // Show prev Chart if multiple is false
    this.actuallStatisticData.nextDate = this.actuallStatisticData.startDate;
    this.actuallStatisticData.startDate = this.actuallStatisticData.prevDate;
    this.actuallStatisticData.nextTrace = this.actuallStatisticData.traces;
    this.actuallStatisticData.traces = this.actuallStatisticData.prevTrace;

    // First show the new data
    this.basicChart();
    // Get more data from Server
    this.loadDataForDate(this.actuallStatisticData.startDate, false);
  }

  nextTimeseriesChart(): void {
    // Show next Chart if multiple is false
    this.actuallStatisticData.prevDate = this.actuallStatisticData.startDate;
    this.actuallStatisticData.startDate = this.actuallStatisticData.nextDate;
    this.actuallStatisticData.prevTrace = this.actuallStatisticData.traces;
    this.actuallStatisticData.traces = this.actuallStatisticData.nextTrace;

    // First show the new data
    this.basicChart();
    // Get more data from the server
    this.loadDataForDate(this.actuallStatisticData.startDate, false);
  }

  datepickerChangeEvent(date: string) {
    console.log("Graphs Component ", date);
    if (date === this.actuallStatisticData.nextDate)
      this.nextTimeseriesChart();
    else if (date === this.actuallStatisticData.prevDate)
      this.prevTimeseriesChart();
    else
      this.loadDataForDate(date, true);
  }

  private loadDataForDate(date: string, rebuildPlotAfterLoad: boolean) {
    this.statisticService.getChartDataForDate(this.chartName, date).subscribe(result => {
      console.log(result);
      this.nextStatisticData = result;
      this.updateLocalStatisticData();
      if(rebuildPlotAfterLoad === true) {
        this.basicChart();
      }
    },
      e => {
        console.log("Could not get Data for Date ", date);
      });
  }

  updateLocalStatisticData() {
    if (this.nextStatisticData != null) {
      console.log("Before");
      console.log("Actuall:", this.actuallStatisticData);
      console.log("Next", this.nextStatisticData);
      if (this.actuallStatisticData.startDate !== this.nextStatisticData.startDate) {
        this.actuallStatisticData.startDate = this.nextStatisticData.startDate;
        this.actuallStatisticData.traces = this.nextStatisticData.traces;
      }
      if (this.actuallStatisticData.nextDate !== this.nextStatisticData.nextDate) {
        this.actuallStatisticData.nextDate = this.nextStatisticData.nextDate;
        this.actuallStatisticData.nextTrace = this.nextStatisticData.nextTrace;
      }
      if (this.actuallStatisticData.prevDate !== this.nextStatisticData.prevDate) {
        this.actuallStatisticData.prevDate = this.nextStatisticData.prevDate;
        this.actuallStatisticData.prevTrace = this.nextStatisticData.prevTrace;
      }
      console.log("After");
      console.log("Actuall:", this.actuallStatisticData);
      console.log("Next", this.nextStatisticData);

    }
  }
}

@Component({
  selector: 'dialog-delete-chart',
  templateUrl: 'dialog-delete-chart.html',
  styleUrls: [ './graphs.component.css' ]
})
export class DialogDeleteChart {

  constructor (
    public dialogRef: MatDialogRef<DialogDeleteChart>) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }
}
