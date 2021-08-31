import { Component, OnInit, Input, OnChanges} from '@angular/core';
import { LoadChartDataService } from '../../services/load-chart-data/load-chart-data.service';
import { ChartDependenciesComponent } from '../../components/chart-dependencies/chart-dependencies.component';
import { SearchComponent } from '../search/search.component';
import {BatchGraphMaker} from "src/app/components/batch-charts/graph-processor/batch-graph-maker";
import * as d3 from 'd3';

@Component({
  selector: 'app-chart-nodeps',
  templateUrl: './chart-nodeps.component.html',
  styleUrls: ['./chart-nodeps.component.css']
})
export class ChartNodepsComponent implements OnInit,OnChanges {


  constructor(private loadChartDataService: LoadChartDataService) { }

  @Input() searchTerm: string;

  private graphDataNodeps: any;

  private onlyCyclesGraphTemp: any;

  private onlyCycleGraph: any;

  private graphDataDiffGraph: any;

  private graphDataDiffGraphTemp:any;

  private normalGraphTemp: any;

  private normalGraph: any;

  private margin = { top: 20, right: 120, bottom: 20, left: 120 };
  private width = 540 - this.margin.right - this.margin.left;
  private height = 600 - this.margin.top - this.margin.bottom;
  private svg_d3;

  private setUpDifferentGraphs(graphData:any){
    let batchGraphMaker = new BatchGraphMaker(graphData);
    this.normalGraphTemp = batchGraphMaker.getNormalGraph();
    this.graphDataDiffGraphTemp = batchGraphMaker.getDifferenceGraph();
    this.onlyCyclesGraphTemp = batchGraphMaker.getGraphCycles();
  }

  ngOnInit(): void {
    this.svg_d3 = d3.select("svg#d3-chart-nodeps")
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);
    
    this.loadChartDataService.chartMessage.subscribe(graphData => {
      if (graphData != null || graphData != undefined) {
        this.setUpDifferentGraphs(graphData);
        this.graphDataDiffGraph = {children:[]};
        for (let node of this.graphDataDiffGraphTemp.nodes) {
            this.graphDataDiffGraph.children.push({Id: node.id, Name: node.label, updateStatus: node.updateStatus, InCycle: node.inCycle, Count: 2000});
          }

          this.onlyCycleGraph = {children:[]};
          for (let node of this.onlyCyclesGraphTemp.nodes) {
              this.onlyCycleGraph.children.push({Id: node.id, Name: node.label, updateStatus: node.updateStatus, InCycle: node.inCycle, Count: 2000});
            }

            this.normalGraph = {children:[]};
            for (let node of this.normalGraphTemp.nodes) {
                this.normalGraph.children.push({Name: node.label, updateStatus: node.updateStatus, inCycle: node.inCycle, Count: 2000});
            }

          this.graphDataNodeps = {children:[]};
          let batchesNodeps = ChartDependenciesComponent.getBatchesNodeps(graphData);
          for (let node of this.normalGraphTemp.nodes) {
            if (batchesNodeps.includes(node.id)) {
              this.graphDataNodeps.children.push({Name: node.label, updateStatus: node.updateStatus, inCycle: node.inCycle, Count: 2000});
            }
          }
        }
        });
  }


  ngOnChanges() {
    this.clear();
    if (this.searchTerm === SearchComponent.SEARCH_RESET) {
      this.clear();
    } else if (this.searchTerm === SearchComponent.SEARCH_FILTER_NODEPS) {
      console.log("DRAW GRAPH FOR SEARCH_FILTER_NODEPS")
      this.drawGraph(this.graphDataNodeps, "normal");
    } else if (this.searchTerm === SearchComponent.SEARCH_GRAPH_CYCLES){
      console.log("GRAPH WITH CYCLES!!")
      this.drawGraph(this.normalGraph, "cycle");
      
    } else if (this.searchTerm === SearchComponent.SEARCH_GRAPH_CYCLES_ONLY){
      console.log("GRAPH CYCLES ONLY!!")
      this.drawGraph(this.onlyCycleGraph, "cycle");
      
    } else if (this.searchTerm === SearchComponent.SEARCH_DIFFERENCE_GRAPH){
      console.log("GRAPH Difference!!")
      this.drawGraph(this.graphDataDiffGraph, "difference");
    }
  }

  private clear() {
    if (this.svg_d3 != null) {
      this.svg_d3.selectAll("*").remove();
    }
  }

  private drawGraph(graphData: any, paintArgument: string) {
    console.log("Drawing normal graph!")
  let diameter = 500;

  let bubble = d3.pack()
      .size([diameter, diameter])
      .padding(1.5);

  let nodes = d3.hierarchy(graphData)
      .sum(function(d) { console.log("CHECKING D!"); console.log(d);return d['Count']; });

  let node = this.svg_d3.selectAll(".node")
      .data(bubble(nodes).descendants())
      .enter()
      .filter(function(d){
          return  !d.children
      })
      .append("g")
      .attr("class", "node")
      .attr("transform", function(d) {
          return "translate(" + d.x + "," + d.y + ")";
      });

  node.append("circle")
      .attr("r", function(d) {
          return d.r;
      })
      //.style("fill", "lightgray");
      .style("fill", function(d){ 
        if(paintArgument === "normal"){
          return "lightgray";
        } else if (paintArgument === "cycle"){
          if(d.data['inCycle']){
            return "red"
          } else {
            return "lightgray"
          }} else if (paintArgument === "difference"){
            if(d.data["updateStatus"] === "same"){
              return "lightgray";
            } else if(d.data["updateStatus"] === "deleted"){
              return "red";
            } else if(d.data["updateStatus"] === "inserted"){
              return "green";
            }
          }
        })

  node.append("text")
      .attr("dy", ".2em")
      .style("text-anchor", "middle")
      .text(function(d) {
          return d.data['Name'];
      })
      .attr("font-family", "sans-serif")
      .attr("font-size", function(d){
          return d.r/3;
      })
      .attr("fill", "black");

  d3.select(self.frameElement)
      .style("height", diameter + "px");
  }

  



}
