import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { StatisticService } from 'src/app/services/statistic/statistic.service';

declare var Plotly: any;

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: [ './graphs.component.css' ]
})
export class GraphsComponent implements OnInit, OnChanges {
  @Input() showGraphChart: boolean;
  @Input() chartName: string;

  @ViewChild("Graph", { static: true })
  private Graph: ElementRef;

  graphData: string;

  pendingResult = false;
  resultAvailable = null;
  configData = null;
  availableFiles = [];
  selectedOption = "";
  selectingGraph = true;

  settingsOpen = false;
  addingEntry = false;
  pendingAddEntry = false;
  newExcel = "beispiel.xlsx";
  newPython = "script.py";

  isDeleteMode = false;
  pendingDelete = false;
  displayedColumns: string[] = [ 'excel', 'py', 'id' ];

  constructor (private statisticService: StatisticService) {

  }
  ngOnChanges(changes: SimpleChanges): void {
    this.loadGraphData();
  }

  loadGraphData() {
    if (this.chartName !== "") {
      this.statisticService.getChartData(this.chartName.replace(/\s/g, "")).subscribe(result => {
        console.log(result)
        if (!(result === null || result === "")) {
          this.selectingGraph = false;
          console.log("should minimize!");
          this.resultAvailable = JSON.stringify(result);
          console.log(JSON.stringify(result));
          this.pendingResult = false;
          this.basicChart();
        }
      });

    }


  }

  basicChart(): void {
    const element = this.Graph.nativeElement;
    const data = JSON.parse(this.graphData);
    const graphData = data[ "traces" ];
    const layout = {
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
    Plotly.newPlot(element, graphData, layout);
  }

  ngOnInit() {
    console.log("Created chart");
    this.loadGraphData();
  }

  csvInputChange(fileInputEvent: any) {
    console.log(fileInputEvent.target.files[ 0 ]);
    let chartname = "anwenderzahlen";
    this.statisticService.updateData(chartname, fileInputEvent.target.files[ 0 ]).subscribe(result => {
      console.log(result);
      this.loadGraphData();
    });
    // Aktuell Ausgewähltes Chart in Kombination mit File zum Server schicken
  }

}
