//import { Graph } from "./graph/graph";
import { CycleGraphSearcher } from "./cycle-graph-searcher/cycle-graph-searcher"
import { DifferenceGraphIdentifier } from './difference-graph-identifier/difference-graph-identifier';
export class GraphProcessor{

    // NODE OBJECT { id: key, name: value.name, label: value.shortName, inCycle: false, updateStatus: "same" }
    //EDGE OBJECT { source: e.predecessor, target: e.successor, inCycle: false, updateStatus: "same", type: 'Next -->>' }
    
      private currentGraphData: any = {
        nodes: [],
        links: [],
        inited: false
      }
      private comparisonGraphData: any = {
        nodes: [],
        links: [],
        inited: false
      }
      
      private graphOutput: any= {
        inited: false,
        nodes: [],
        links: [],
        directDependantNodes:[]
      };
    
    
      constructor(graphData: any, graphDataToCompare: any){
        this.currentGraphData.inited  = graphData.inited;
        this.currentGraphData.nodes  = graphData.nodes;
        this.currentGraphData.links  = graphData.links;
        
        this.comparisonGraphData.inited  = graphDataToCompare.inited;
        this.comparisonGraphData.nodes  = graphDataToCompare.nodes;
        this.comparisonGraphData.links  = graphDataToCompare.links;
      }

      public processAndReturnGraphData(){
        if(this.currentGraphData.inited){
        // Difference Graph search
        let differenceIdentifier = new DifferenceGraphIdentifier(this.currentGraphData, this.comparisonGraphData);
        console.log("fetching difference graph")
        this.graphOutput = differenceIdentifier.getDifferenceGraphNodesAndEdges();
        // Graph Cycle Search
        let graph = differenceIdentifier.getCurrentGraph();
        console.log("fethching cycles from graph")
        let cycleIdentifier = new CycleGraphSearcher(graph);
        cycleIdentifier.startSearchingForCycles();
        this.graphOutput = cycleIdentifier.updateGraphWithInCycleData(this.graphOutput);
        }
        return this.graphOutput;
      }
    
    }