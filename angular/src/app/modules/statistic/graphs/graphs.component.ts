import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, Output, EventEmitter, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SpinnerService } from 'src/app/services/spinner/spinner.service';
import { StatisticService } from '../services/statistic.service';
import { StatisticChart } from '../model/statisticChart';
import { StatisticAccuracy } from '../model/statisticAccuracy';
import { StatisticData, DEFAULT_LAYOUT } from '../model/statisticData';
import { DialogDeleteChart } from './dialog-delete-chart.component';

declare var Plotly: any;

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: [ './graphs.component.css' ]
})
export class GraphsComponent implements OnInit, OnChanges, OnDestroy {
  @Input() showGraphChart: boolean;
  @Input() chart: StatisticChart;
  @Input() chartName: string;

  @Output() notifyDeleteChart = new EventEmitter<boolean>();

  @ViewChild("Graph")
  private plotlyGraph: ElementRef;

  graphData: string;

  actualStatisticData: StatisticData = null;
  nextStatisticData: StatisticData = null;

  updateTime: string = "";
  fileUploadText: string = "Daten aktualisieren";
  showUploadAreaFile: boolean = false;

  // DatePicker Properties
  datePickerTitle = "Datum";
  timeSeriesDates: string[] = [];
  prevChartsTooltip: string = "Vorherigen Datensatz anzeigen";
  nextChartsTooltip: string = "Nächsten Datensatz anzeigen";


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

  ngOnDestroy(): void {
    this.destroyPlot();
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
          this.actualStatisticData = result;
          this.nextStatisticData = result;
          this.parseChartData();
        }
        this.spinnerService.hide();
      }, e => {
        this.spinnerService.hide();
        console.log("Could not get Data from backend!", e);
      });

      if (this.isTimeseries()) {
        this.statisticService.getTimeseriesDates(this.chartName).subscribe(result => {
          this.timeSeriesDates = result;
        }, e => {
          console.log("Could not get Timestamps for timeseries!", e);
        });
      }
    }
  }

  parseChartData() {
    if (this.actualStatisticData.layout === null || this.nextStatisticData.layout === undefined) {
      this.actualStatisticData.layout = DEFAULT_LAYOUT;
    }
    this.actualStatisticData.layout[ 'title' ] = this.actualStatisticData.title;
    this.updateTime = this.actualStatisticData.updateTime;
    this.updateTooltips();
    this.basicChart();
  }

  updateTooltips() {
    if (this.isMultiple()) {
      if (this.actualStatisticData.prevTrace) {
        this.prevChartsTooltip = "Vorherige " + this.actualStatisticData.prevTrace.length + " Datensätze anzeigen";
      }
      if (this.actualStatisticData.nextTrace) {
        this.nextChartsTooltip = "Nächsten " + this.actualStatisticData.nextTrace.length + " Datensätze anzeigen";
      }
    }
  }

  basicChart(): void {
    this.destroyPlot();
    const element = this.plotlyGraph.nativeElement;
    const graphData = this.actualStatisticData.traces;
    const layout = this.actualStatisticData.layout;
    Plotly.newPlot(element, graphData, layout);
  }

  ngOnInit() {
    this.loadInitialGraphData();
  }

  onFileAdded(file) {
    this.spinnerService.show();
    this.statisticService.updateData(this.chartName, file).subscribe(result => {
      this.loadInitialGraphData();
      this.spinnerService.hide();
    }, e => {
      console.log("Could not add file", e);
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
    // Show prev Chart
    this.actualStatisticData.nextDate = this.actualStatisticData.startDate;
    this.actualStatisticData.startDate = this.actualStatisticData.prevDate;
    this.actualStatisticData.nextTrace = this.actualStatisticData.traces;
    this.actualStatisticData.traces = this.actualStatisticData.prevTrace;
    this.actualStatisticData.nextEndDate = this.actualStatisticData.endDate;
    this.actualStatisticData.endDate = this.actualStatisticData.prevEndDate;
    // First show the new data
    this.updateTooltips();
    this.basicChart();
    // Get more data from Server
    this.loadDataForDate(this.actualStatisticData.startDate, this.actualStatisticData.endDate, false);
  }

  nextTimeseriesChart(): void {
    // Show next Chart 
    this.actualStatisticData.prevDate = this.actualStatisticData.startDate;
    this.actualStatisticData.startDate = this.actualStatisticData.nextDate;
    this.actualStatisticData.prevTrace = this.actualStatisticData.traces;
    this.actualStatisticData.traces = this.actualStatisticData.nextTrace;
    this.actualStatisticData.prevEndDate = this.actualStatisticData.endDate;
    this.actualStatisticData.endDate = this.actualStatisticData.nextEndDate;
    // First show the new data
    this.updateTooltips();
    this.basicChart();
    // Get more data from the server
    this.loadDataForDate(this.actualStatisticData.startDate, this.actualStatisticData.endDate, false);
  }

  datepickerChangeEvent(date: Map<string, string>) {
    if (this.isMultiple()) {
      let startDate = date.get('start');
      let endDate = date.get('end');
      if (startDate === this.actualStatisticData.nextDate && endDate === this.actualStatisticData.nextEndDate)
        this.nextTimeseriesChart();
      else if (startDate === this.actualStatisticData.prevDate && endDate === this.actualStatisticData.prevEndDate)
        this.prevTimeseriesChart();
      else
        this.loadDataForDate(startDate, endDate, true);
    } else {
      if (date.get('start') === this.actualStatisticData.nextDate)
        this.nextTimeseriesChart();
      else if (date.get('start') === this.actualStatisticData.prevDate)
        this.prevTimeseriesChart();
      else
        this.loadDataForDate(date.get('start'), "", true);
    }
  }

  private loadDataForDate(startdate: string, enddate: string, rebuildPlotAfterLoad: boolean) {
    this.statisticService.getChartDataForDate(this.chartName, startdate, enddate).subscribe(result => {
      this.nextStatisticData = result;
      this.updateLocalStatisticData();
      if (rebuildPlotAfterLoad === true) {
        this.basicChart();
      }
    },
      e => {
        console.log("Could not get Data for Date ", startdate, e);
      });
  }

  updateLocalStatisticData() {
    if (this.nextStatisticData != null) {
      this.actualStatisticData.startDate = this.nextStatisticData.startDate;
      this.actualStatisticData.traces = this.nextStatisticData.traces;
      if (this.actualStatisticData.nextDate !== this.nextStatisticData.nextDate) {
        this.actualStatisticData.nextDate = this.nextStatisticData.nextDate;
        this.actualStatisticData.nextTrace = this.nextStatisticData.nextTrace;
      }
      if (this.actualStatisticData.prevDate !== this.nextStatisticData.prevDate) {
        this.actualStatisticData.prevDate = this.nextStatisticData.prevDate;
        this.actualStatisticData.prevTrace = this.nextStatisticData.prevTrace;
      }
      if (this.isMultiple) {
        this.actualStatisticData.endDate = this.nextStatisticData.endDate;
        this.actualStatisticData.nextEndDate = this.nextStatisticData.nextEndDate;
        this.actualStatisticData.prevEndDate = this.nextStatisticData.prevEndDate;
      }
      this.updateTooltips();
    }
  }
}
