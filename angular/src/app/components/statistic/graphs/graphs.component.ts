import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, Output, EventEmitter, Inject } from '@angular/core';
import { StatisticService } from 'src/app/services/statistic/statistic.service';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { SpinnerService } from 'src/app/services/spinner/spinner.service';

declare var Plotly: any;

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: [ './graphs.component.css' ]
})
export class GraphsComponent implements OnInit, OnChanges {
  @Input() showGraphChart: boolean;
  @Input() chartName: string;

  @Output() notifyDeleteChart = new EventEmitter<boolean>();

  @ViewChild("Graph")
  private Graph: ElementRef;

  graphData: string;

  pendingResult = false;
  resultAvailable = null;
  selectingGraph = true;

  updateTime: string = "";

  constructor (private statisticService: StatisticService, public dialog: MatDialog, private spinnerService: SpinnerService) {

  }
  ngOnChanges(changes: SimpleChanges): void {
    this.destroyPlot();
    this.updateTime = "";
    this.loadGraphData();
  }

  destroyPlot() {
    if (this.Graph && this.Graph.nativeElement)
      Plotly.purge(this.Graph.nativeElement);
  }

  loadGraphData() {
    if (this.chartName !== "") {
      this.spinnerService.show();
      this.statisticService.getChartData(this.chartName.replace(/\s/g, "")).subscribe(result => {
        console.log(result);
        if (!(result === null || result === "")) {
          this.selectingGraph = false;
          console.log("should minimize!");
          this.resultAvailable = JSON.stringify(result);
          console.log(JSON.stringify(result));
          this.pendingResult = false;
          this.basicChart();
        }
        this.spinnerService.hide();
      });

    }
  }

  basicChart(): void {
    const element = this.Graph.nativeElement;
    const data = JSON.parse(this.resultAvailable);
    const graphData = data[ "traces" ];
    let layout;
    if (data.updateTime)
      this.updateTime = data.updateTime;
    if (data.layout) {
      layout = data.layout;
      layout.title = data.title;
    }
    else {
      layout = {
        xaxis: {
          showline: true,
          showgrid: true,
          showticklabels: true,
          linewidth: 2,
          ticks: 'outside',
          tickfont: {
            family: 'Arial',
            size: 12,
          }
        },
        yaxis: {
          showgrid: true,
          zeroline: true,
          showline: true,
          showticklabels: true,
        },
        autosize: true,
        margin: {
          autoexpand: true,
          l: 100,
          r: 20,
          t: 110,
        },
        showlegend: true,
        title: data[ "title" ]
      };
    }
    Plotly.newPlot(element, graphData, layout);
  }

  ngOnInit() {
    console.log("Created chart");
    this.loadGraphData();
  }

  dataSourceInputChange(fileInputEvent: any) {
    console.log(fileInputEvent.target.files[ 0 ]);
    this.spinnerService.show();
    this.statisticService.updateData(this.chartName, fileInputEvent.target.files[ 0 ]).subscribe(result => {
      console.log(result);
      this.loadGraphData();
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
