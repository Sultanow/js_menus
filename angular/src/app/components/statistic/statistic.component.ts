import { Component, OnInit, ViewChild, ViewEncapsulation, ElementRef, Input } from '@angular/core';
import * as d3 from 'd3';
import { Batches } from 'src/app/model/batches';

@Component({
  selector: 'app-statistic',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css']
})
export class StatisticComponent implements OnInit {

  @ViewChild('chart', {static: false})
  chartElement: ElementRef;

  parseDate = d3.timeParse('%d-%m-%Y');

  @Input()
  showStatistic: boolean;
  @Input() 
  batchtimes: Batches[];

  private svgElement: HTMLElement;
  private chartProps: any;
  
  constructor() {

    
    
  }

  ngOnInit() {
  }

ngAfterViewInit() {
  console.log("Statistic AVI");
  if (this.batchtimes) {
    this.buildChart();
  }
}

  ngOnChanges() {
    
  }
 
  formatDate() {
    this.batchtimes.forEach(ms => {
      if (typeof ms.date === 'string') {
        ms.date = this.parseDate(ms.date);
      }
    });
  }

  buildChart() {
    this.chartProps = {};
    this.formatDate();
  
    // Set the dimensions of the canvas / graph
    var margin = { top: 30, right: 20, bottom: 30, left: 50 },
      width = 600 - margin.left - margin.right,
      height = 270 - margin.top - margin.bottom;
  
    // Set the ranges
    this.chartProps.x = d3.scaleTime().range([0, width]);
    this.chartProps.y = d3.scaleLinear().range([height, 0]);
  
    // Define the axes
    var xAxis = d3.axisBottom(this.chartProps.x);
    var yAxis = d3.axisLeft(this.chartProps.y).ticks(5);
  
    let _this = this;
  
    // Define the line
    var valueline = d3.line<Batches>()
      .x(function (d) {
        if (d.date instanceof Date) {
          return _this.chartProps.x(d.date.getTime());
        }
      })
      .y(function (d) { console.log('Durachtion Batch 1'); return _this.chartProps.y(d.durationBatch1); })
      ;
  
    // Define the line
    var valueline2 = d3.line<Batches>()
      .x(function (d) {
        if (d.date instanceof Date) {
          return _this.chartProps.x(d.date.getTime());
        }
      })
      .y(function (d) { console.log('Duration Batch 2'); return _this.chartProps.y(d.durationBatch2); });
    console.log("try to append svg");
    var svg = d3.select(this.chartElement.nativeElement)
      .append('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom)
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);
  
    // Scale the range of the data
    this.chartProps.x.domain(
      d3.extent(_this.batchtimes, function (d) {
        if (d.date instanceof Date)
          return (d.date as Date).getTime();
      }));
    this.chartProps.y.domain([0, d3.max(this.batchtimes, function (d) {
      return Math.max(d.durationBatch1, d.durationBatch2);
    })]);
  
    // Add the valueline2 path.
    svg.append('path')
      .attr('class', 'line line2')
      .style('stroke', 'lightgreen')
      .style('fill', 'none')
      .attr('d', valueline2(_this.batchtimes));
  
    // Add the valueline path.
    svg.append('path')
      .attr('class', 'line line1')
      .style('stroke', 'blue')
      .style('fill', 'none')
      .attr('d', valueline(_this.batchtimes));
  
      svg.append("circle").attr("cx",200).attr("cy",130).attr("r", 6).style("fill", "lightgreen")
      svg.append("circle").attr("cx",200).attr("cy",160).attr("r", 6).style("fill", "blue")
      svg.append("text")
        .attr("x", 220)
        .attr("y", 130)
        .text("Batch 1")
        .style("font-size", "15px")
        .style("fill", "lightgreen")
        .attr("alignment-baseline","middle")

      svg.append("text")
        .attr("x", 220)
        .attr("y", 160)
        .text("Batch 2")
        .style("font-size", "15px")
        .style("fill", "blue")
        .attr("alignment-baseline","middle")
      
    // Add the X Axis
    svg.append('g')
      .attr('class', 'x axis')
      .attr('transform', `translate(0,${height})`)
      .call(xAxis);
  
    // Add the Y Axis
    svg.append('g')
      .attr('class', 'y axis')
      .call(yAxis);
  
    // Setting the required objects in chartProps so they could be used to update the chart
    this.chartProps.svg = svg;
    this.chartProps.valueline = valueline;
    this.chartProps.valueline2 = valueline2;
    this.chartProps.xAxis = xAxis;
    this.chartProps.yAxis = yAxis;
  }
}
