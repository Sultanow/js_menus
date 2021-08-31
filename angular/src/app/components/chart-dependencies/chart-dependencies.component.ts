import { Component, OnInit, Input, OnChanges, SimpleChanges, NgZone } from '@angular/core';
import { LoadChartDataService } from '../../services/load-chart-data/load-chart-data.service';
import { SearchComponent } from '../search/search.component';
import * as d3 from 'd3';
import { BatchInfoDialogComponent } from '../batch-info-dialog/batch-info-dialog.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { BatchService } from 'src/app/services/batches/batches.service';
import {BatchGraphMaker} from "src/app/components/batch-charts/graph-processor/batch-graph-maker";
import Swal from "sweetalert2";

@Component({
  selector: 'app-chart-dependencies',
  templateUrl: './chart-dependencies.component.html',
  styleUrls: ['./chart-dependencies.component.css']
})
export class ChartDependenciesComponent implements OnInit, OnChanges {

  constructor(private loadChartDataService: LoadChartDataService, private dialog: MatDialog,
    private batchService: BatchService, private ngZone: NgZone) { }

  public static readonly LABEL_DICT: Map<string, string> = new Map([
    ["Parallelisierung", "Parall."],
    ["Synchronisierung(.+)?", "Sync."],
    ["Verzweigung(.+)?", "Verzw."],
    ["Verbindung", "Verb."],
    ["Aktivitätsende", "Ende"]
  ]);

  tableStructure = [];

  public static readonly NODE_PARALLELISIERUNG = "fc1db4a1-dbbc-db62-a574-eb92da4a606b";

  @Input() searchTerm: string;

  private graphData: any;
  private graphDataNodeps: any;

  private onlyCycleGraphData: any;

  private differenceGraphData: any;

  private margin = { top: 20, right: 120, bottom: 20, left: 120 };
  private width = 960 - this.margin.right - this.margin.left;
  private height = 600 - this.margin.top - this.margin.bottom;
  private svg_d3;
  batches: any[];
  currentNode: String = "";

  ngOnInit() {
    this.svg_d3 = d3.select("svg#d3-chart-dependencies")
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);

    this.loadChartDataService.chartMessage.subscribe(graphData => {
      if (graphData != null || graphData != undefined) {
        console.log("Graph received for visualization!");
        this.graphData = graphData;
        //Checking for direct dependencies in graph for Warning Message
        this.checkIfThereAreDirectCircularDependenciesInGraph();
        //Setting up all types of graphs for visualisation: Normal Graph, Difference Graph, Graph with Cycles, Only cycles out of the graph
        this.setUpDifferentGraphs();
        this.drawGraph(this.graphData, "normal");
        this.graphDataNodeps = ChartDependenciesComponent.getGraphDataNodeps(graphData);
        console.log(this.graphData);
        console.log("done graph data")
      }
    });
    this.getBatches();
    this.batchService.getTableStructure().subscribe(struct => {
      this.tableStructure = JSON.parse(struct);
    })
  }

  private setUpDifferentGraphs(){
    let batchGraphMaker = new BatchGraphMaker(this.graphData);
    this.graphData = batchGraphMaker.getNormalGraph();
    this.differenceGraphData = batchGraphMaker.getDifferenceGraph();
    this.onlyCycleGraphData = batchGraphMaker.getGraphCycles();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.searchTerm.isFirstChange()) return;
    this.clear();
    if (this.searchTerm === SearchComponent.SEARCH_RESET) {
      console.log("ngOnChanges SEARCH_RESET")
      this.drawGraph(this.graphData, "normal");
    } else if (this.searchTerm === SearchComponent.SEARCH_FILTER_NODEPS) {
      console.log("ngOnChanges SEARCH_FILTER_NODEPS")
     this.drawGraph(this.graphDataNodeps, "normal");
    }else if(this.searchTerm === SearchComponent.SEARCH_GRAPH_CYCLES){
      console.log("ngOnChanges SEARCH_GRAPH_CYCLES")
      console.log("Drawing grapph with cyccles!")
      this.drawGraph(this.graphData, "cycle");
    }else if (this.searchTerm === SearchComponent.SEARCH_GRAPH_CYCLES_ONLY){
      console.log("ngOnChanges SEARCH_GRAPH_CYCLES_ONLY")
      console.log("Drawing cyclces ONLY !")
      this.drawGraph(this.onlyCycleGraphData, "cycle"); 
    }  else if (this.searchTerm === SearchComponent.SEARCH_DIFFERENCE_GRAPH){
      console.log("ngOnChanges SEARCH_DIFFERENCE_GRAPH")
      console.log("Drawing difference graph!")
      this.drawGraph(this.differenceGraphData, "difference");
    }else if (this.searchTerm != null && this.searchTerm.length > 2) {
      let treeData = {};
      let root = this.searchNode(this.searchTerm);
      this.constructTree(root, treeData);
      this.drawTree(treeData);
    } else {
      if (this.svg_d3 != null) {
       this.drawGraph(this.graphData, "normal");
      }
    }
  }

  private clear() {
    if (this.svg_d3 != null) {
      this.svg_d3.selectAll("*").remove();
    }
  }
  getBatches(): void {
    this.batchService.getBatches()
      .subscribe(batches => {console.log("batches", batches); 
      this.batches = batches
    });
  }
  private showBatch(id: string) {
    this.currentNode = (ChartDependenciesComponent.getNode(id, this.graphData)['label']);
    // https://github.com/angular/components/issues/7550#issuecomment-345250406

    this.ngZone.run(() => {
        this.openDialog(this.batches.filter(b => b.id == this.currentNode)[0]);
    })
  }

  private openDialog(batch) {

    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      batchData: batch,
      tableStructure: this.tableStructure
    }
    console.log("opening dialog");
    
    let dialogRef = this.dialog.open(BatchInfoDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(r => this.dialog.closeAll())    
  }

  /*
   * Bubble Chart
   */
  public static getBatchesNodeps(graphData: any): Array<string> {
    let result: Array<string> = [];
    for (let node of graphData.nodes) {
      for (let link of graphData.links) {
        if ((link.source.id === node.id && link.target.id === ChartDependenciesComponent.NODE_PARALLELISIERUNG)
          && (ChartDependenciesComponent.searchPredecessors(node.id, graphData).length == 0)) {
          result.push(node.id);
        }
      }
    }
    return result;
  }

  private checkIfThereAreDirectCircularDependenciesInGraph(){
    if(this.graphData.directDependantNodes.length != 0){
      let warningMessage = "Following jobs dependet on each other: "
      for(let i = 0; i<this.graphData.directDependantNodes.length; i++){
        let node1 = this.graphData.directDependantNodes[i][0];
        let node2 = this.graphData.directDependantNodes[i][1];
        warningMessage += "\n " + node1.label + " ==> " + node2.label;
      }
      this.openSweetAlert(warningMessage);
    }
  }

  public openSweetAlert(errorMessage:string){
    Swal.fire({
      title: 'Batch jobs with direct circular dependencies have been found!',
      text: errorMessage,
      icon: 'warning',
      showCancelButton: false,
      confirmButtonText: 'Continue',
      cancelButtonText: 'No, keep it'
    })
  }

  private static getGraphDataNodeps(graphData: any): any {
    let batchesNodeps = ChartDependenciesComponent.getBatchesNodeps(graphData);
    let graphDataNodeps = {
      inited: true,
      nodes: [],
      links: []
    };
    for (let node of graphData.nodes) {
      if (!batchesNodeps.includes(node.id)) {
        graphDataNodeps.nodes.push(node);
      }
    }
    for (let link of graphData.links) {
      if (!batchesNodeps.includes(link.source.id) && !batchesNodeps.includes(link.target.id)) {
        graphDataNodeps.links.push(link);
      }
    }
    return graphDataNodeps;
  }

  /*
   * Tree
   */
  private static getNode(id: string, graphData: any): d3.SimulationLinkDatum<d3.SimulationNodeDatum> {
    for (let node of graphData.nodes) {
      if (node.id == id) {
        return node;
      }
    }
    return null;
  }

  private searchNode(shortName: string): d3.SimulationLinkDatum<d3.SimulationNodeDatum> {
    for (let node of this.graphData.nodes) {
      if (node.label.toLowerCase() === shortName.toLowerCase()) {
        return node;
      }
    }
    return null;
  }

  private static searchPredecessors(id: string, graphData: any): d3.SimulationLinkDatum<d3.SimulationNodeDatum>[] {
    let result = [];
    graphData.links.forEach(e => {
      //before drawing the structure is e.[source|target]
      if (e.target.id === id) {
        result.push(e.source);
      }
    });
    return result;
  }

  private searchSuccessors(id: string): d3.SimulationLinkDatum<d3.SimulationNodeDatum>[] {
    let result = [];
    this.graphData.links.forEach(e => {
      //before drawing the structure is e.[source|target].id
      if (e.source.id == id) {
        result.push(e.target);
      }
    });
    return result;
  }

  private abbrev(label: string): string {
    for (let [key, value] of ChartDependenciesComponent.LABEL_DICT) {
      let regexp = new RegExp(key);
      if (regexp.test(label)) {
        return value;
      }
    }
    return label;
  }

  private constructTree(nodeSrc: any, nodeTgt: any) {
    if (typeof nodeSrc !== 'undefined') {
      nodeTgt.label = nodeSrc.label;
      nodeTgt.id = nodeSrc.id;
      let successors = this.searchSuccessors(nodeSrc.id);
      if (successors.length > 0) {
        nodeTgt.children = [];
        successors.forEach((e, i) => {
          let successor = ChartDependenciesComponent.getNode(e['id'], this.graphData);
          nodeTgt.children.push(successor);
          this.constructTree(successor, nodeTgt.children[i]);
        });
      }
    }
  }

  private drawTree(treeData: any) {
    const treemap = d3.tree().size([this.height, this.width]);
    let nodes = d3.hierarchy(treeData, d => d.children);
    nodes = treemap(nodes);
    // build the arrow.
    this.svg_d3.append("svg:defs").selectAll("marker")
      .data(["end"])
      .enter().append("svg:marker")
      .attr("id", String)
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 25)
      .attr("refY", 0)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto-start-reverse")
      .append("svg:path")
      .attr("d", "M0,-5L10,0L0,5");

    const g = this.svg_d3.append("g").attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

    g.selectAll(".link").data(nodes.descendants().slice(1)).enter().append("path")
      .attr("class", "link")
      .attr("fill", "none")
      .style("stroke", d => "black")
      .style("stroke-width", 2)
      .attr("d", function (d: any) {
        return "M" + (d.y - 0) + "," + d.x
          + "C" + (d.y + d.parent.y) / 2 + "," + (d.x)
          + " " + (d.y + d.parent.y) / 2 + "," + (d.parent.x)
          + " " + (d.parent.y) + "," + (d.parent.x);
      })
      .attr("marker-start", "url(#end)");

    const node = g.selectAll(".node").data(nodes.descendants()).enter().append("g")
      .attr("class", d => "node" + (d.children ? " node--internal" : " node--leaf"))
      .attr("transform", function (d: any) { return "translate(" + d.y + "," + d.x + ")"; })
      .on("click", (event, d: d3.SimulationLinkDatum<d3.SimulationNodeDatum>) => {
        this.showBatch(d['data']['id']);
      });

    node.append("rect")
      .attr("width", 40)
      .attr("height", 20)
      .attr("transform", function (d) { return "translate(-20,-10)" })
      .classed("textbox", true)

    node.append("text")
      .attr("x", -18)
      .attr("dy", ".3em")
      .text(d => this.abbrev(d.data.label))
      .classed("text", true)
  }

  private drawGraph(graphData: any, paintArgument: string) {
    // Diffferent graphs have to be drawn in a different way -> the "paintArgument" decides, which style will be painted (example difference graph with deleted edges and nodes as red and added nodes and edges as green)
    let simulation = d3.forceSimulation<any, any>();
    simulation.force("link", d3.forceLink().id(function (d, i) { return d['id']; }).distance(100).strength(1))
      .force("charge", d3.forceManyBody())
      .force("center", d3.forceCenter(this.width / 2, this.height / 2));

    // build the arrow.
    this.svg_d3.append("svg:defs").selectAll("marker")
      .data(["end"])
      .enter().append("svg:marker")
      .attr("id", String)
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 20)
      .attr("refY", 0)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto")
      .append("svg:path")
      .attr("d", "M0,-5L10,0L0,5");

    let link = this.svg_d3.append("g").selectAll(".link")
      .attr("class", "link")
      .data(graphData.links)
      .enter()
      .append("line")
      .attr("stroke", function(d){ 
        if(paintArgument === "normal"){
          return "black";
        } else if (paintArgument === "cycle"){
          if(d.inCycle === true){
            return "red"
          } else {
            return "black"
          }} else if (paintArgument === "difference"){
            if(d.updateStatus === "same"){
              return "black";
            } else if(d.updateStatus === "deleted"){
              return "red";
            } else if(d.updateStatus === "inserted"){
              return "green";
            }
          }
        })
      .style("stroke-width", 2)
      .attr("marker-end", "url(#end)");

    let node = this.svg_d3.selectAll(".node")
      .data(graphData.nodes)
      .enter()
      .append("g")
      .on("click", (event, d: d3.SimulationLinkDatum<d3.SimulationNodeDatum>) => {
        this.showBatch(d['id']);
      })
      // .on("mouseout", () => {this.currentNode="";this._cdr.detectChanges();})
      .call(d3.drag()
        .on("start", function (event, d: d3.SimulationLinkDatum<d3.SimulationNodeDatum>) {
          if (!event.active) simulation.alphaTarget(0.3).restart()
          d['fx'] = d['x'];
          d['fy'] = d['y'];
        })
        .on("drag", function (event, d) {
          d['fx'] = event.x;
          d['fy'] = event.y;
        }));


    node.append("rect")
      .attr("width", 40)
      .attr("height", 20)
      .attr("transform", function (d) { return "translate(-20,-10)" })
      .classed("textbox", true)
      .style("stroke", function(d){ 
        if(paintArgument === "normal"){
          return "black";
        } else if (paintArgument === "cycle"){
          if(d.inCycle === true){
            return "red"
          } else {
            return "black"
          }} else if (paintArgument === "difference"){
            if(d.updateStatus === "same"){
              return "black";
            } else if(d.updateStatus === "deleted"){
              return "red";
            } else if(d.updateStatus === "inserted"){
              return "green";
            }
          }
        })

    node.append("text")
      .attr("x", -18)
      .attr("dy", ".3em")
      .classed("text", true)
      .style("color", "black")
      .text(function (d) { return d.label });

    // set the width of the rect to the text width + padding
    node.selectAll('rect')
      .attr("width", function (d) { return this.parentNode.getBBox().width + 5; })
    let ticked = function () {
      link
        .attr("x1", function (d) { return d.source.x; })
        .attr("y1", function (d) { return d.source.y; })
        .attr("x2", function (d) { return d.target.x; })
        .attr("y2", function (d) { return d.target.y; });

      node
        .attr("transform", function (d) { return "translate(" + d.x + ", " + d.y + ")"; });
    }
    simulation.nodes(graphData.nodes).on("tick", ticked);
    simulation.nodes(graphData.nodes);
    simulation.force<d3.ForceLink<any, any>>("link").links(graphData.links);
  }


 
}
