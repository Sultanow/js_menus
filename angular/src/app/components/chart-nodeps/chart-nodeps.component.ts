import { Component, OnInit, Input, OnChanges} from '@angular/core';
import { LoadChartDataService } from '../../services/load-chart-data/load-chart-data.service';
import { ChartDependenciesComponent } from '../../components/chart-dependencies/chart-dependencies.component';
import { SearchComponent } from '../search/search.component';
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

  private margin = { top: 20, right: 120, bottom: 20, left: 120 };
  private width = 540 - this.margin.right - this.margin.left;
  private height = 600 - this.margin.top - this.margin.bottom;
  private svg_d3;

  ngOnInit(): void {
    this.svg_d3 = d3.select("svg#d3-chart-nodeps")
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);
    
    this.loadChartDataService.chartMessage.subscribe(graphData => {
      if (graphData != null) {
        this.graphDataNodeps = {children:[]};
        let batchesNodeps = ChartDependenciesComponent.getBatchesNodeps(graphData);
        for (let node of graphData.nodes) {
          if (batchesNodeps.includes(node.id)) {
            this.graphDataNodeps.children.push({Name: node.label, Count: 2000});
          }
        }
      }});
  }

  ngOnChanges() {
    this.clear();
    if (this.searchTerm === SearchComponent.SEARCH_RESET) {
      this.clear();
    } else if (this.searchTerm === SearchComponent.SEARCH_FILTER_NODEPS) {
      this.drawGraph(this.graphDataNodeps);
    }
  }

  private clear() {
    if (this.svg_d3 != null) {
      this.svg_d3.selectAll("*").remove();
    }
  }

  private drawGraph(graphData: any) {

  let diameter = 500;

  let bubble = d3.pack()
      .size([diameter, diameter])
      .padding(1.5);

  let nodes = d3.hierarchy(graphData)
      .sum(function(d) { return d['Count']; });

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
      .style("fill", "lightgray");

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
